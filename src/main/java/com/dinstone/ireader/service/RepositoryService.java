
package com.dinstone.ireader.service;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.dinstone.ireader.Configuration;
import com.dinstone.ireader.domain.Article;
import com.dinstone.ireader.domain.Category;
import com.dinstone.ireader.domain.Repository;

@Service
public class RepositoryService {

    private static final Logger LOG = LoggerFactory.getLogger(RepositoryService.class);

    private final JacksonSerializer serializer = new JacksonSerializer();

    private IndexSearcher indexSearcher;

    private Repository repository;

    @Autowired
    private Configuration configuration;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AsyncService asyncService;

    public Repository createRepository() throws Exception {
        LOG.info("创建文章库开始");
        // 抽取分类
        List<Category> categories = categoryService.extractCategories();
        Repository repository = new Repository();
        for (Category category : categories) {
            repository.categoryMap.put(category.id, category);
        }
        LOG.info("创建文章库完成");
        return repository;
    }

    public void updateRepository() throws Exception {
        if (repository == null) {
            repository = loadRepository();
            if (repository == null) {
                repository = new Repository();
            }
        }
        updateRepositoryContent(repository);
    }

    private void updateRepositoryContent(Repository repository) {
        LOG.info("更新文章库开始");

        try {
            List<Category> categories = categoryService.extractCategories();
            refreshRepository(repository, categories);

            LOG.info("更新文章库完成");

            writeRepository(repository);
        } catch (Exception e) {
            LOG.warn("update repository error", e);
        }

    }

    private Repository loadRepository() {
        File dataFile = new File(configuration.getRepositoryDir(), "categories.data");
        if (dataFile.exists()) {
            BufferedReader reader = null;
            try {
                LOG.info("开始加载文章库从本地文件系统：{}", dataFile);
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile), "utf-8"));
                List<Category> categories = new LinkedList<Category>();
                String temp = null;
                while ((temp = reader.readLine()) != null) {
                    Category category = serializer.deserialize(temp.getBytes("utf-8"), Category.class);
                    categoryService.loadCategory(category);
                    categories.add(category);
                }
                LOG.info("完成加载文章库从本地文件系统：{}", dataFile);

                Repository repository = new Repository();
                for (Category category : categories) {
                    repository.categoryMap.put(category.id, category);
                }
                return repository;
            } catch (Exception e) {
                LOG.warn("加载文章库从本地文件系统出错：{}", dataFile, e);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ignored) {
                    }
                }
            }

        }

        return null;
    }

    private void writeRepository(Repository repository) {
        File dataFile = new File(configuration.getRepositoryDir(), "categories.data");
        BufferedOutputStream writer = null;
        try {
            if (!dataFile.exists()) {
                dataFile.getParentFile().mkdirs();
            }
            LOG.info("回写文章库到本地文件系统开始：{}", dataFile);

            writer = new BufferedOutputStream(new FileOutputStream(dataFile));
            for (Category category : repository.categoryMap.values()) {
                if (category.persistent) {
                    //
                    categoryService.writeCategory(category);

                    byte[] bytes = serializer.serialize(category);
                    writer.write(bytes);
                    writer.write('\r');
                    writer.write('\n');
                    writer.flush();
                }
            }

            LOG.info("回写文章库到本地文件系统完成：{}", dataFile);
        } catch (Exception e) {
            LOG.warn("回写文章库到本地文件系统出错：{}", dataFile, e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                }
            }
        }

        String repositoryDir = configuration.getRepositoryDir();
        File file = new File(repositoryDir, "index");
        try {
            Directory directory = FSDirectory.open(file.toPath());
            LOG.info("更新文章库的索引开始：{}", file);

            IndexWriterConfig config = new IndexWriterConfig(new IKAnalyzer());
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            IndexWriter indexWriter = new IndexWriter(directory, config);

            List<Document> docs = new LinkedList<>();
            for (Article article : repository.articleMap.values()) {
                //创建文档对象
                Document document = new Document();
                //向文档对象中添加域
                document.add(new TextField("author", article.author, Field.Store.YES));
                document.add(new TextField("title", article.name, Field.Store.YES));
                //StringField会创建索引，但是不会被分词，TextField，即创建索引又会被分词。
                document.add(new StringField("id", article.id, Field.Store.YES));
//                document.add(new StoredField("path", article.href));
//                document.add(new StoredField("status", article.status));
//                document.add(new StoredField("category", article.category.name));

                docs.add(document);
            }
            indexWriter.addDocuments(docs);
            indexWriter.commit();
            indexWriter.close();

            LOG.info("更新文章库的索引结束：{}", file);
        } catch (Exception e) {
            LOG.warn("更新文章库的索引出错：{}", file, e);
        }

        indexSearcher = null;
    }

    /**
     * 刷新文章库,从远程抓取最新文章到库中
     *
     * @param repository
     * @param categories
     */
    private void refreshRepository(Repository repository, List<Category> categories) {
        for (Category category : categories) {
            Category other = repository.categoryMap.get(category.id);
            if (other == null) {
                repository.categoryMap.put(category.id, category);
            } else {
                other.name = category.name;
                other.href = category.href;
            }
        }

        // 构建分类中的文章
        for (Category category : repository.categoryMap.values()) {
            categoryService.buildCategory(category);

            // 将文章放入文章库
            repository.articleMap.putAll(category.articleMap);
        }

        // 初始化全集
        Category finCategory = new Category("artc_98", "完本", false);
        for (Article article : repository.articleMap.values()) {
            if ("完本".equals(article.status)) {
                finCategory.addArticle(article);
            }
        }
        repository.categoryMap.put(finCategory.id, finCategory);

//        Category topCategory = new Category("artc_99", "排行榜", false);
//        categoryService.buildTopCategory(repository, topCategory);
//        repository.categoryMap.put(topCategory.id, topCategory);
    }

    public List<Article> queryRepository(String word) {
        String repositoryDir = configuration.getRepositoryDir();
        File file = new File(repositoryDir, "index");
        if (indexSearcher == null) {
            //1、创建一个Director对象，指定索引库的位置
            try {
                Directory directory = FSDirectory.open(file.toPath());
                //2、创建一个IndexReader对象
                IndexReader indexReader = DirectoryReader.open(directory);
                //3、创建一个IndexSearcher对象，构造方法中的参数indexReader对象。
                indexSearcher = new IndexSearcher(indexReader);
            } catch (Exception e) {
                LOG.warn("创建文章库的索引出错：{}", file, e);
                return Collections.emptyList();
            }
        }

        try {
            String[] kws = {"title", "author"};
            MultiFieldQueryParser parser = new MultiFieldQueryParser(kws, new IKAnalyzer());
            TopDocs topDocs = indexSearcher.search(parser.parse(word), 100);
            //7、取文档列表
            List<Article> as = new ArrayList<>((int) topDocs.totalHits.value);
            //8、打印文档中的内容
            for (ScoreDoc doc : topDocs.scoreDocs) {
                //根据id取文档对象
                Document document = indexSearcher.doc(doc.doc);
                String articleId = document.get("id");
                Article article = repository.articleMap.get(articleId);
                if (article != null) {
                    as.add(article);
                }
            }
            return as;
        } catch (Exception e) {
            LOG.warn("搜索文章库索引出错：{}", file, e);
            return Collections.emptyList();
        }
    }

    public Repository getRepository() {
        return repository;
    }
}
