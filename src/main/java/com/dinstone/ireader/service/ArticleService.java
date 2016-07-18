
package com.dinstone.ireader.service;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;
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
import com.dinstone.ireader.domain.Part;
import com.dinstone.ireader.domain.Repository;

@Service
public class ArticleService {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleService.class);

    private JacksonSerializer serializer = new JacksonSerializer();

    @Resource
    private Configuration configuration;

    @Resource
    private AsyncService asyncService;

    public Article findAticle(Repository repository, String articleId) {
        Article article = repository.articleMap.get(articleId);
        if (article == null) {
            return null;
        }

        if (article.parts == null) {
            // load from local file system
            article.parts = loadDerectory(article);
            //
            if (article.parts == null) {
                try {
                    article.parts = extractDirectory(article);
                    writeDirectory(article);
                } catch (Exception e) {
                    LOG.warn("update article directory error", e);
                }
            }

            asyncService.updateArticle(new ArticleUpdater(article, configuration, this));
        } else if (needUpdate(article.update)) {
            asyncService.updateArticle(new ArticleUpdater(article, configuration, this));
        }
        return article;
    }

    public void writeDirectory(Article article) {
        File dataFile = new File(configuration.getRepositoryDir(), article.category.id + "/" + article.id
                + "/parts.data");
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
        }

        if (article.parts == null) {
            return;
        }

        LOG.info("更新文章[{}]目录到本地文件系统开始：{}", article.name, dataFile);
        BufferedOutputStream writer = null;
        try {
            writer = new BufferedOutputStream(new FileOutputStream(dataFile));
            for (Part part : article.parts) {
                byte[] bytes = serializer.serialize(part);
                writer.write(bytes);
                writer.write('\r');
                writer.write('\n');
                writer.flush();
            }

            LOG.info("更新文章[{}]目录到本地文件系统完成：{}", article.name, dataFile);
        } catch (Exception e) {
            LOG.warn("更新文章[{}]目录到本地文件系统失败：{}, {}", article.name, dataFile, e.getMessage());
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public Part[] loadDerectory(Article article) {
        File dataFile = new File(configuration.getRepositoryDir(), article.category.id + "/" + article.id
                + "/parts.data");
        LOG.info("加载文章[{}]目录从本地文件系统开始：{}", article.name, dataFile);
        BufferedReader reader = null;
        try {
            List<Part> partList = new LinkedList<Part>();
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile), "utf-8"));
            String temp = null;
            while ((temp = reader.readLine()) != null) {
                Part part = serializer.deserialize(temp.getBytes("utf-8"), Part.class);
                partList.add(part);
            }

            LOG.info("加载文章[{}]目录从本地文件系统完成：{}", article.name, dataFile);

            partList.toArray(new Part[0]);
        } catch (Exception e) {
            LOG.warn("加载文章[{}]目录从本地文件系统失败：{}, {}", article.name, dataFile, e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }

        return null;

    }

    public Part[] extractDirectory(Article article) throws Exception {
        String url = article.href;
        int tryCount = 1;
        while (true) {
            try {
                String referrer = article.category.href;
                String userAgent = configuration.getUserAgent();
                Document doc = Jsoup.connect(url).referrer(referrer).userAgent(userAgent).timeout(5000).get();

                // extract parts
                List<Part> parts = new LinkedList<Part>();
                Elements links = doc.select("a[href]");
                for (Element link : links) {
                    String href = link.attr("abs:href");
                    if (href.contains("read_")) {
                        String name = link.text();
                        int index = parseIndex(name);

                        Part part = new Part();
                        part.index = index;
                        part.url = href;

                        parts.add(part);
                    }
                }

                Part[] array = parts.toArray(new Part[parts.size()]);
                Arrays.sort(array);

                return array;
            } catch (Exception e) {
                tryCount++;
                if (tryCount > 3) {
                    throw e;
                }
            }
        }
    }

    private int parseIndex(String name) {
        try {
            return Integer.parseInt(name.replaceAll("\\D+", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    private boolean needUpdate(Date update) {
        if (update == null) {
            return true;
        }

        long diff = new Date().getTime() - update.getTime();
        int interval = configuration.getUpdateInterval();
        if (diff > interval * 1000) {
            return true;
        }

        return false;
    }

    public File getPartFile(Article article, Part part) {
        File cfile = new File(configuration.getRepositoryDir(), article.category.id);
        return new File(cfile, article.id + "/" + part.index + ".txt");
    }

}
