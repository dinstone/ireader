
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
import com.dinstone.ireader.domain.Repository;

@Service
public class CategoryService {

    private static final Logger LOG = LoggerFactory.getLogger(CategoryService.class);

    private JacksonSerializer serializer = new JacksonSerializer();

    @Resource
    private Configuration configuration;

    @Resource
    private AsyncService asyncService;

    public List<Category> extractCategorys() throws Exception {
        List<Category> categories = new LinkedList<Category>();
        String catalogUrl = "http://www.yi-see.com/";
        int tryCount = 1;
        while (true) {
            try {
                String userAgent = configuration.getUserAgent();
                Document doc = Jsoup.connect(catalogUrl).userAgent(userAgent).timeout(5000).get();

                Elements links = doc.select("div.header a[href]");
                for (Element link : links) {
                    String href = link.attr("href");
                    if (href.contains("artc_")) {
                        Category cat = new Category();
                        cat.id = href.substring(1).replaceAll(".html", "");
                        cat.name = link.text();
                        cat.href = link.attr("abs:href");
                        if (!"artc_0".equals(cat.id)) {
                            categories.add(cat);
                        }
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

        LOG.info("同步文章分类 : {}", categories);
        return categories;
    }

    public void buildTopCategroy(Repository repository, Category category) {
        long s = System.currentTimeMillis();

        int pageIndex = 1;
        String next = "http://www.yi-see.com/weeksort_1.html";
        int maxPageNumber = configuration.getTopMaxPageNumber();
        while (next != null && pageIndex < maxPageNumber) {
            LOG.info("build category[{}] page[{}] from {} ", category.name, pageIndex, next);

            try {
                next = collectTopArticles(repository, category, pageIndex, next);
            } catch (Exception e) {
                LOG.warn("build category[{}] page[{}] error", category.name, pageIndex);
                break;
            }

            pageIndex++;
        }

        long e = System.currentTimeMillis();
        LOG.info("build category[{}] take {}s", category.name, (e - s) / 1000);
    }

    private String collectTopArticles(Repository repository, Category category, int pageIndex, String access)
            throws Exception {
        int tryCount = 1;
        while (true) {
            try {
                String userAgent = configuration.getUserAgent();
                Document doc = Jsoup.connect(access).userAgent(userAgent).timeout(5000).get();
                Elements tables = doc.select("table");
                if (tables.size() >= 2) {
                    Elements trs = tables.get(2).select("tbody tr");
                    for (Element tr : trs) {
                        Elements tds = tr.children();
                        Element nameLink = tds.first().select("div.T2 a[href],div.T1 a[href]").first();
                        if (nameLink != null) {
                            String href = nameLink.attr("href");
                            if (href.contains("art_")) {
                                String articleId = href.replaceAll(".html", "");
                                Article article = repository.articleMap.get(articleId);
                                if (article != null) {
                                    category.addArticle(article);
                                }
                            }
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

                return next;
            } catch (Exception e) {
                tryCount++;
                if (tryCount > 3) {
                    throw e;
                }
            }
        }
    }

    public void buildCategroy(Category category) {
        long s = System.currentTimeMillis();

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
                String userAgent = configuration.getUserAgent();
                Document doc = Jsoup.connect(access).userAgent(userAgent).timeout(5000).get();
                Elements tables = doc.select("table");
                if (tables.size() >= 2) {
                    Elements trs = tables.get(2).select("tbody tr");
                    for (Element tr : trs) {
                        Elements tds = tr.children();
                        Element nameLink = tds.first().select("div.T2 a[href],div.T1 a[href]").first();
                        if (nameLink != null) {
                            String href = nameLink.attr("href");
                            if (href.contains("art_")) {
                                String articleId = href.replaceAll(".html", "");
                                Article article = category.getArticle(articleId);
                                if (article != null) {
                                    article.category = category;

                                    continue;
                                }

                                article = new Article();
                                // base info
                                article.id = articleId;
                                article.name = nameLink.text();
                                article.href = nameLink.attr("abs:href");
                                article.category = category;

                                // auth info
                                Element authLink = tds.get(1).select("div.Auth a[href]").first();
                                if (authLink != null) {
                                    article.author = authLink.text();
                                }

                                // status info
                                String status = tds.get(2).text();
                                if (status != null && status.contains("全文完")) {
                                    article.status = "全文完";
                                } else {
                                    article.status = "连载中";
                                }

                                category.addArticle(article);
                            }
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

                return next;
            } catch (Exception e) {
                tryCount++;
                if (tryCount > 3) {
                    throw e;
                }
            }
        }

    }

    public void writeCategory(Category category) {
        File dataFile = new File(configuration.getRepositoryDir(), category.id + "/articles.data");
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
        }

        LOG.info("回写分类[{}]文章到本地文件系统开始：{}", category.name, dataFile);
        BufferedOutputStream writer = null;
        try {
            writer = new BufferedOutputStream(new FileOutputStream(dataFile));
            for (Article article : category.articleList) {
                byte[] bytes = serializer.serialize(article);
                writer.write(bytes);
                writer.write('\r');
                writer.write('\n');
                writer.flush();
            }

            LOG.info("回写分类[{}]文章到本地文件系统完成：{}", category.name, dataFile);
        } catch (Exception e) {
            LOG.warn("回写分类[{}]文章到本地文件系统失败：{}, {}", category.name, dataFile, e.getMessage());
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public void loadCategory(Category category) {
        File dataFile = new File(configuration.getRepositoryDir(), category.id + "/articles.data");
        LOG.info("加载分类[{}]文章从本地文件系统开始：{}", category.name, dataFile);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile), "utf-8"));
            String temp = null;
            while ((temp = reader.readLine()) != null) {
                Article article = serializer.deserialize(temp.getBytes("utf-8"), Article.class);
                category.addArticle(article);
            }

            LOG.info("加载分类[{}]文章从本地文件系统完成：{}", category.name, dataFile);
        } catch (Exception e) {
            LOG.warn("加载分类[{}]文章从本地文件系统失败：{}, {}", category.name, dataFile, e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
