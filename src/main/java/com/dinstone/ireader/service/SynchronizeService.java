
package com.dinstone.ireader.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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

    @Resource
    private Configuration configuration;

    public ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 3);

    public void extractCategorys() throws Exception {
        Map<String, Category> categoryMap = Repository.getInstance().categoryMap;

        String catalogUrl = "http://www.yi-see.com/";
        int tryCount = 1;
        while (true) {
            try {
                Document doc = Jsoup.connect(catalogUrl).get();

                Elements links = doc.select("div.header a[href]");
                for (Element link : links) {
                    String href = link.attr("href");
                    if (href.contains("artc_")) {
                        Category cat = new Category();
                        cat.id = href.substring(1).replaceAll(".html", "");
                        cat.name = link.text();
                        cat.href = link.attr("abs:href");

                        categoryMap.put(cat.id, cat);
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

        List<Category> categories = new ArrayList<Category>(categoryMap.values().size());
        categories.addAll(categoryMap.values());
        Collections.sort(categories);
        Repository.getInstance().categorys = categories;

        LOG.info("同步文章分类 : {}", categories);
    }

    public void extractArticles() throws Exception {
        long s = System.currentTimeMillis();

        Category topCategory = Repository.getInstance().topCategory;

        int pageIndex = 1;
        String next = "http://www.yi-see.com/weeksort_1.html";
        int maxPageNumber = configuration.getMaxPageNumber();
        while (next != null && pageIndex < maxPageNumber) {
            LOG.info("extract articles from next page:{} ", next);

            Pagenation pagenation = extractArticlePagenation(next, pageIndex);
            topCategory.pages.add(pagenation);

            next = pagenation.next;
            pageIndex++;
        }

        long e = System.currentTimeMillis();
        LOG.info("extract articles {}s", (e - s) / 1000);
    }

    private Pagenation extractArticlePagenation(String access, int pageIndex) throws Exception {
        Pagenation pagenation = new Pagenation();
        pagenation.access = access;
        pagenation.index = pageIndex;

        Map<String, Article> articlesMap = Repository.getInstance().articlesMap;

        int tryCount = 1;
        while (true) {
            try {
                Document doc = Jsoup.connect(access).get();

                List<Article> pageArticles = new LinkedList<Article>();
                Elements links = doc.select("div.T2 a[href],div.T1 a[href]");
                for (Element link : links) {
                    String href = link.attr("href");
                    if (href.contains("art_")) {
                        Article article = new Article();
                        article.id = href.replaceAll(".html", "");
                        article.name = link.text();
                        article.href = link.attr("abs:href");

                        articlesMap.put(article.id, article);

                        pageArticles.add(article);
                    }
                }

                String next = null;
                Elements nexts = doc.select("div.NEXT a[href]");
                for (Element link : nexts) {
                    String name = link.text();
                    if (name.contains("下一页")) {
                        next = link.attr("abs:href");
                    }
                }

                pagenation.articles = pageArticles;
                pagenation.next = next;

                break;
            } catch (Exception e) {
                tryCount++;
                if (tryCount > 3) {
                    throw e;
                }
            }
        }

        return pagenation;
    }

    public void updateArticles() {
        List<Pagenation> pages = Repository.getInstance().topCategory.pages;
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

    public void buildCategroys() throws Exception {
        for (Category category : Repository.getInstance().categoryMap.values()) {
            buildCategroy(category);
        }
    }

    private void buildCategroy(Category category) throws Exception {
        long s = System.currentTimeMillis();

        int pageIndex = 1;
        String next = category.href;
        int maxPageNumber = configuration.getMaxPageNumber();
        while (next != null && pageIndex < maxPageNumber) {
            LOG.info("build category[{}] from next page:{} ", category.name, next);

            Pagenation pagenation = buildCategoryPagenation(next, pageIndex);
            category.pages.add(pagenation);

            next = pagenation.next;
            pageIndex++;
        }

        long e = System.currentTimeMillis();
        LOG.info("build category[{}] take {}s", category.name, (e - s) / 1000);
    }

    private Pagenation buildCategoryPagenation(String access, int pageIndex) throws Exception {
        Map<String, Article> articlesMap = Repository.getInstance().articlesMap;

        Pagenation pagenation = new Pagenation();
        pagenation.access = access;
        pagenation.index = pageIndex;

        int tryCount = 1;
        while (true) {
            try {
                Document doc = Jsoup.connect(access).get();

                List<Article> articles = new LinkedList<Article>();
                Elements links = doc.select("div.T2 a[href],div.T1 a[href]");
                for (Element link : links) {
                    String href = link.attr("href");
                    if (href.contains("art_")) {
                        String id = href.replaceAll(".html", "");
                        Article article = articlesMap.get(id);
                        if (article != null) {
                            articles.add(article);
                        }
                    }
                }

                String next = null;
                Elements nexts = doc.select("div.NEXT a[href]");
                for (Element link : nexts) {
                    String name = link.text();
                    if (name.contains("下一页")) {
                        next = link.attr("abs:href");
                    }
                }

                pagenation.articles = articles;
                pagenation.next = next;

                break;
            } catch (Exception e) {
                tryCount++;
                if (tryCount > 3) {
                    throw e;
                }
            }
        }

        return pagenation;
    }

    private void destroy() {
        executor.shutdownNow();
        try {
            executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }
    }

    public static void main(String[] args) throws Exception {
        SynchronizeService service = new SynchronizeService();
        service.extractCategorys();
        // service.extractArticles();
        // service.updateArticles();
        // service.buildCategroys();
        //
        // Repository repository = Repository.getInstance();
        // System.out.println(repository);

        System.in.read();

        service.destroy();
    }

    public Article findAticle(String articleId) {
        Article article = Repository.getInstance().articlesMap.get(articleId);
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
}
