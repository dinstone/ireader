
package com.dinstone.ireader.service;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dinstone.ireader.Configuration;
import com.dinstone.ireader.domain.Article;
import com.dinstone.ireader.domain.Category;
import com.dinstone.ireader.domain.Repository;

@Service
public class RepositoryService {

    private static final Logger LOG = LoggerFactory.getLogger(RepositoryService.class);

    private JacksonSerializer serializer = new JacksonSerializer();

    @Autowired
    private Configuration configuration;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AsyncService asyncService;

    public Repository createRepository() throws Exception {
        LOG.info("创建文章库开始");
        Repository repository = new Repository();

        // 抽取分类
        List<Category> categories = categoryService.extractCategories();
        // 刷新文章库
        refreshRepository(repository, categories);

        LOG.info("创建文章库完成");

        writeRepository(repository);

        return repository;
    }

    public void updateRepository(Repository repository) {
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

    public Repository loadRepository() {
        File dataFile = new File(configuration.getRepositoryDir(), "categories.data");
        if (dataFile.exists()) {
            BufferedReader reader = null;
            try {
                LOG.info("开始加载文章库从本地文件系统：{}", dataFile);
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile), "utf-8"));
                List<Category> categorys = new LinkedList<Category>();
                String temp = null;
                while ((temp = reader.readLine()) != null) {
                    Category category = serializer.deserialize(temp.getBytes("utf-8"), Category.class);
                    categoryService.loadCategory(category);
                    categorys.add(category);
                }

                LOG.info("完成加载文章库从本地文件系统：{}", dataFile);

                Repository repository = new Repository();
                refreshRepository(repository, categorys);

                writeRepository(repository);

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
        BufferedOutputStream writer = null;
        try {
            File dataFile = new File(configuration.getRepositoryDir(), "categories.data");
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

    /**
     * 刷新文章库,从远程抓取最新文章到库中
     *
     * @param repository
     * @param categories
     */
    protected void refreshRepository(Repository repository, List<Category> categories) {
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

}
