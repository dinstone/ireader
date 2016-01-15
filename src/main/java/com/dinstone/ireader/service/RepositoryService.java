
package com.dinstone.ireader.service;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dinstone.ireader.Configuration;
import com.dinstone.ireader.domain.Article;
import com.dinstone.ireader.domain.Category;
import com.dinstone.ireader.domain.Pagenation;
import com.dinstone.ireader.domain.Repository;

@Service
public class RepositoryService {

    private static final Logger LOG = LoggerFactory.getLogger(RepositoryService.class);

    private JacksonSerializer serializer = new JacksonSerializer();

    @Resource
    private Configuration configuration;

    @Resource
    private CategoryService categoryService;

    @Resource
    private AsyncService asyncService;

    public Repository createRepository() throws Exception {
        LOG.info("创建文章库开始");
        Repository repository = new Repository();

        List<Category> categories = categoryService.extractCategorys();
        for (Category category : categories) {
            if (!repository.categoryMap.containsKey(category.id)) {
                repository.categoryMap.put(category.id, category);
            }
        }
        repository.categorys = repository.categoryMap.values();

        for (Category category : repository.categorys) {
            categoryService.buildCategroy(category);

            repository.articleMap.putAll(category.articleMap);
        }

        categoryService.buildTopCategory(repository);

        writeRepository(repository);

        LOG.info("创建文章库完成");
        return repository;
    }

    public void updateRepository(Repository repository) {
        if (repository == null) {
            LOG.warn("无效的文章库对象：{}", repository);
            return;
        }

        try {
            List<Category> categories = categoryService.extractCategorys();
            for (Category category : categories) {
                if (!repository.categoryMap.containsKey(category.id)) {
                    repository.categoryMap.put(category.id, category);
                }
            }
            repository.categorys = repository.categoryMap.values();

            for (Category category : repository.categorys) {
                categoryService.buildCategroy(category);

                repository.articleMap.putAll(category.articleMap);
            }

            categoryService.buildTopCategory(repository);

            repository.updateTime = new Date();

            writeRepository(repository);

            updateArticles(repository);
        } catch (Exception e) {
            LOG.warn("update repository error", e);
        }

    }

    private void updateArticles(Repository repository) {
        for (Article article : repository.articleMap.values()) {
            asyncService.updateArticle(article);
        }
    }

    public Repository loadRepository() {
        File dataFile = new File(configuration.getRepositoryDir(), "repository.data");
        if (dataFile.exists()) {
            BufferedReader reader = null;
            try {
                LOG.info("加载文章库从本地文件系统开始：{}", dataFile);
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile), "utf-8"));
                String temp = null;
                List<Category> categorys = new LinkedList<Category>();
                while ((temp = reader.readLine()) != null) {
                    Category category = serializer.deserialize(temp.getBytes("utf-8"), Category.class);
                    categorys.add(category);
                }

                LOG.info("加载文章库从本地文件系统完成：{}", dataFile);

                Repository repository = new Repository();

                LOG.info("初始化文章库开始");
                for (Category category : categorys) {
                    repository.categoryMap.put(category.id, category);

                    for (Pagenation page : category.pages) {
                        for (Article article : page.articles) {
                            article.category = category;
                            category.articleMap.put(article.id, article);
                        }
                    }

                    repository.articleMap.putAll(category.articleMap);
                }

                repository.categorys = repository.categoryMap.values();

                categoryService.buildTopCategory(repository);
                LOG.info("初始化文章库完成");

                return repository;
            } catch (Exception e) {
                LOG.warn("加载文章库从本地文件系统出错：{}", e);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                    }
                }
            }

        }

        return null;
    }

    public void writeRepository(Repository repository) {
        if (repository == null) {
            LOG.warn("无效的文章库对象：{}", repository);
            return;
        }

        BufferedOutputStream writer = null;
        try {
            File dataFile = new File(configuration.getRepositoryDir(), "repository.data");
            if (!dataFile.exists()) {
                dataFile.getParentFile().mkdirs();
            }
            LOG.info("回写文章库到本地文件系统开始：{}", dataFile);

            writer = new BufferedOutputStream(new FileOutputStream(dataFile));
            for (Category category : repository.categorys) {
                byte[] bytes = serializer.serialize(category);
                writer.write(bytes);
                writer.write('\r');
                writer.write('\n');
                writer.flush();
            }

            LOG.info("回写文章库到本地文件系统完成：{}", dataFile);
        } catch (Exception e) {
            LOG.warn("回写文章库到本地文件系统出错：{}", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public Article findAticle(Repository repository, String articleId) {
        Article article = repository.articleMap.get(articleId);
        if (article != null && needUpdate(article.update)) {
            asyncService.updateArticle(article);
        }
        return article;
    }

    private boolean needUpdate(Date update) {
        if (update == null) {
            return true;
        }

        long diff = new Date().getTime() - update.getTime();
        if (diff > 24 * 60 * 60 * 1000) {
            return true;
        }

        return false;
    }

}
