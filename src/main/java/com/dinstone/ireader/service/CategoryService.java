
package com.dinstone.ireader.service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
public class CategoryService {

    private static final Logger LOG = LoggerFactory.getLogger(CategoryService.class);

    @Resource
    private Configuration configuration;

    @Resource
    private AsyncService asyncService;

    public void buildTopCategory(Repository repository) {
        long s = System.currentTimeMillis();

        String next = "http://www.yi-see.com/weeksort_1.html";
        int pageIndex = 1;
        Category category = repository.topCategory;
        category.pages.clear();
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

    public void buildCategroy(Category category) {
        long s = System.currentTimeMillis();

        category.pages.clear();

        int pageIndex = 1;
        String next = category.href;
        int maxPageNumber = configuration.getMaxPageNumber();
        while (next != null && pageIndex < maxPageNumber) {
            LOG.info("build category[{}] page[{}] from {} ", category.name, pageIndex, next);

            try {
                next = collectPagenationArticles(category, pageIndex, next);
            } catch (Exception e) {
                LOG.warn("build category[{}] page[{}] error", category.name, pageIndex);
                break;
            }

            pageIndex++;
        }

        long e = System.currentTimeMillis();
        LOG.info("build category[{}] take {}s", category.name, (e - s) / 1000);
    }

    private String collectPagenationArticles(Category category, int pageIndex, String access) throws Exception {
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
                        Article article = category.articleMap.get(id);
                        if (article == null) {
                            article = new Article();
                            article.id = id;
                            article.name = link.text();
                            article.href = link.attr("abs:href");
                            article.category = category;
                            category.articleMap.put(id, article);
                        }

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
}
