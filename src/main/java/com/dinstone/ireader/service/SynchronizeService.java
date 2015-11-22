
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
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dinstone.ireader.Configuration;
import com.dinstone.ireader.domain.Article;
import com.dinstone.ireader.domain.Category;
import com.dinstone.ireader.domain.Pagenation;
import com.dinstone.ireader.domain.Repository;

@Service
public class SynchronizeService {

    private static final Logger LOG = LoggerFactory.getLogger(SynchronizeService.class);

    private JacksonSerializer serializer = new JacksonSerializer();

    @Resource
    private Configuration configuration;

    public ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 3);

    public List<Category> extractCategorys() throws Exception {
        List<Category> categories = new LinkedList<Category>();
        String catalogUrl = "http://www.yi-see.com/";
        int tryCount = 1;
        while (true) {
            try {
                Document doc = Jsoup.connect(catalogUrl).timeout(5000).get();

                Elements links = doc.select("div.header a[href]");
                for (Element link : links) {
                    String href = link.attr("href");
                    if (href.contains("artc_")) {
                        Category cat = new Category();
                        cat.id = href.substring(1).replaceAll(".html", "");
                        cat.name = link.text();
                        cat.href = link.attr("abs:href");

                        categories.add(cat);
                    }
                }

                break;
            } catch (Exception e) {
                tryCount++;
                if (tryCount > 3) {
                    throw e;
                }
            }
        }

        Collections.sort(categories);

        LOG.info("同步文章分类 : {}", categories);
        return categories;
    }

    public void updateArticles() {
        List<Pagenation> pages = RepositoryManager.getInstance().getRepository().topCategory.pages;
        List<Future<Pagenation>> futures = new ArrayList<Future<Pagenation>>();
        for (Pagenation page : pages) {
            Future<Pagenation> future = executor.submit(new PagenationUpdater(page));
            futures.add(future);
        }

        for (Future<Pagenation> future : futures) {
            try {
                Pagenation page = future.get();
                LOG.info("update pagenation[{}] finish", page.index);
            } catch (InterruptedException e) {
                break;
            } catch (ExecutionException e) {
            }
        }
    }

    private List<Category> buildCategorys() throws Exception {
        List<Category> categories = extractCategorys();
        for (Category category : categories) {
            buildCategroy(category);
        }
        return categories;
    }

    private void buildCategroy(Category category) {
        long s = System.currentTimeMillis();

        int pageIndex = 1;
        String next = category.href;
        int maxPageNumber = configuration.getMaxPageNumber();
        while (next != null && pageIndex < maxPageNumber) {
            LOG.info("build category[{}] page[{}] from {} ", category.name, pageIndex, next);

            try {
                next = buildCategoryPagenation(category, pageIndex, next);
            } catch (Exception e) {
                LOG.warn("build category[{}] page[{}] error", category.name, pageIndex);
                break;
            }

            pageIndex++;
        }

        long e = System.currentTimeMillis();
        LOG.info("build category[{}] take {}s", category.name, (e - s) / 1000);
    }

    private String buildCategoryPagenation(Category category, int pageIndex, String access) throws Exception {
        int tryCount = 1;
        while (true) {
            try {
                Document doc = Jsoup.connect(access).timeout(5000).get();

                List<Article> articles = new LinkedList<Article>();
                Elements links = doc.select("div.T2 a[href],div.T1 a[href]");
                for (Element link : links) {
                    String href = link.attr("href");
                    if (href.contains("art_")) {
                        Article article = new Article();
                        article.id = href.replaceAll(".html", "");
                        article.name = link.text();
                        article.href = link.attr("abs:href");
                        article.category = category;
                        articles.add(article);
                    }
                }

                String next = null;
                Elements nexts = doc.select("div.NEXT a[href]");
                for (Element link : nexts) {
                    String name = link.text();
                    if (name.contains("下一页")) {
                        next = link.attr("abs:href");
                        break;
                    }
                }

                Pagenation pagenation = new Pagenation();
                pagenation.access = access;
                pagenation.index = pageIndex;
                pagenation.articles = articles;
                category.pages.add(pagenation);

                return next;
            } catch (Exception e) {
                tryCount++;
                if (tryCount > 3) {
                    throw e;
                }
            }
        }

    }

    private void destroy() {
        executor.shutdownNow();
        try {
            executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }
    }

    public Article findAticle(String articleId) {
        Repository repository = RepositoryManager.getInstance().getRepository();
        Article article = repository.articleMap.get(articleId);
        if (article != null && needUpdate(article.update)) {
            Future<Article> future = executor.submit(new ArticleUpdater(article, configuration));
            try {
                future.get(5, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                return null;
            }
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

    public Repository createRepository() throws Exception {
        List<Category> categorys = buildCategorys();

        Map<String, Category> categoryMap = new HashMap<String, Category>();
        Map<String, Article> articleMap = new HashMap<String, Article>();
        for (Category category : categorys) {
            categoryMap.put(category.id, category);
            for (Pagenation page : category.pages) {
                for (Article article : page.articles) {
                    articleMap.put(article.id, article);
                }
            }
        }

        Repository repository = new Repository();
        repository.categorys = categorys;
        repository.categoryMap = categoryMap;
        repository.articleMap = articleMap;
        return repository;
    }

    public Repository loadRepository() {
        List<Category> categorys = new LinkedList<Category>();
        File dataFile = new File(configuration.getRepositoryDir(), "repository.data");
        if (dataFile.exists()) {
            BufferedReader reader = null;
            try {
                LOG.info("加载文章库从本地文件系统开始：{}", dataFile);
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile), "utf-8"));
                String temp = null;
                while ((temp = reader.readLine()) != null) {
                    Category category = serializer.deserialize(temp.getBytes("utf-8"), Category.class);
                    categorys.add(category);
                }

                LOG.info("加载文章库从本地文件系统完成：{}", dataFile);

                Map<String, Category> categoryMap = new HashMap<String, Category>();
                Map<String, Article> articleMap = new HashMap<String, Article>();
                for (Category category : categorys) {
                    categoryMap.put(category.id, category);
                    for (Pagenation page : category.pages) {
                        for (Article article : page.articles) {
                            article.category = category;
                            articleMap.put(article.id, article);
                        }
                    }
                }

                if (categorys.size() > 0) {
                    Repository repository = new Repository();
                    repository.categorys = categorys;
                    repository.categoryMap = categoryMap;
                    repository.articleMap = articleMap;
                    return repository;
                }
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

    public void buildTopCategory(Repository repository) {
        long s = System.currentTimeMillis();

        String next = "http://www.yi-see.com/weeksort_1.html";
        int pageIndex = 1;
        Category category = repository.topCategory;
        while (next != null && pageIndex < 3) {
            LOG.info("build category[{}] page[{}] from {} ", category.name, pageIndex, next);

            try {
                next = buildTopCategoryPagenation(repository.articleMap, category, pageIndex, next);
            } catch (Exception e) {
                LOG.warn("build category[{}] page[{}] error", category.name, pageIndex);
                break;
            }

            pageIndex++;
        }

        long e = System.currentTimeMillis();
        LOG.info("build category[{}] take {}s", category.name, (e - s) / 1000);
    }

    private String buildTopCategoryPagenation(Map<String, Article> articleMap, Category category, int pageIndex,
            String access) throws Exception {
        int tryCount = 1;
        while (true) {
            try {
                Document doc = Jsoup.connect(access).timeout(5000).get();

                List<Article> articles = new LinkedList<Article>();
                Elements links = doc.select("div.T2 a[href],div.T1 a[href]");
                for (Element link : links) {
                    String href = link.attr("href");
                    if (href.contains("art_")) {
                        String id = href.replaceAll(".html", "");
                        String name = link.text();
                        Article article = articleMap.get(id);
                        if (article != null) {
                            articles.add(article);
                        } else {
                            LOG.warn("unkown article {}:{}", id, name);
                        }
                    }
                }

                String next = null;
                Elements nexts = doc.select("div.NEXT a[href]");
                for (Element link : nexts) {
                    String name = link.text();
                    if (name.contains("下一页")) {
                        next = link.attr("abs:href");
                        break;
                    }
                }

                Pagenation pagenation = new Pagenation();
                pagenation.access = access;
                pagenation.index = pageIndex;
                pagenation.articles = articles;
                category.pages.add(pagenation);

                return next;
            } catch (Exception e) {
                tryCount++;
                if (tryCount > 3) {
                    throw e;
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        SynchronizeService service = new SynchronizeService();
        service.configuration = new Configuration("ireader-config.xml");
        Repository repository = service.loadRepository();
        if (repository == null) {
            repository = service.createRepository();
            service.writeRepository(repository);
        }

        // service.build

        // service.extractCategorys();
        // service.extractArticles();
        // service.updateArticles();
        // service.buildCategroys();
        //
        // Repository repository = Repository.getInstance();
        // System.out.println(repository);

        System.in.read();

        service.destroy();
    }
}
