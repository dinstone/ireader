
package com.dinstone.ireader.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.concurrent.Callable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.ireader.Configuration;
import com.dinstone.ireader.domain.Article;
import com.dinstone.ireader.domain.Part;

public class ArticleUpdater implements Callable<Article> {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleUpdater.class);

    private String userAgent;

    private Article article;

    private File articleDir;

    private ArticleService articleService;

    public ArticleUpdater(Article article, Configuration config, ArticleService articleService) {
        this.article = article;
        this.userAgent = config.getUserAgent();
        this.articleService = articleService;
        this.articleDir = new File(config.getRepositoryDir(), article.category.id + "/" + article.id);
    }

    @Override
    public Article call() throws Exception {
        if (article.proccess) {
            return article;
        }

        update();

        return article;
    }

    public void update() {
        article.proccess = true;
        try {
            LOG.info("更新文章[{}]目录开始: {}", article.name, article.href);

            article.parts = articleService.extractDirectory(article);
            articleService.writeDirectory(article);

            LOG.info("更新文章[{}]目录完成: {}", article.name, article.href);
        } catch (Exception e) {
            LOG.error("更新文章[{}]目录失败:{} ", article.name, e.getMessage());
            return;
        }

        File metaFile = new File(articleDir, article.id + ".meta");
        int index = loadUpdatePartIndex(metaFile);

        BufferedWriter writer = null;
        try {
            File articleFile = new File(articleDir, article.id + ".txt");
            articleFile.getParentFile().mkdirs();
            article.file = articleFile;

            LOG.info("同步文章[{}]开始: {}", article.name, article.href);

            if (article.parts != null) {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(articleFile, true), "utf-8"));
                int length = article.parts.length;
                for (; index < length; index++) {
                    Part part = article.parts[index];

                    LOG.info("同步文章[{}]第{}/{}节", article.name, part.index, length);

                    String content = extractContent(article, part);

                    writer.write("第" + part.index + "节");
                    writer.newLine();
                    writer.write(content);
                    writer.newLine();
                    writer.flush();
                }
            }

            article.update = new Date();
            LOG.info("同步文章[{}]完成: {}", article.name, articleFile);
        } catch (Exception e) {
            LOG.warn("同步文章[{}]第{}节时失败:{}", article.name, index, e.getMessage());
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                }
            }
        }

        writeUpdatePartIndex(index, metaFile);

        article.proccess = false;
    }

    private void writeUpdatePartIndex(int index, File metaFile) {
        if (!metaFile.exists()) {
            metaFile.getParentFile().mkdirs();
        }

        BufferedWriter metaWriter = null;
        try {
            metaWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(metaFile)));
            metaWriter.write("" + index);
            metaWriter.flush();
        } catch (Exception e) {
        } finally {
            if (metaWriter != null) {
                try {
                    metaWriter.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private int loadUpdatePartIndex(File metaFile) {
        if (!metaFile.exists()) {
            metaFile.getParentFile().mkdirs();
            return 0;
        }

        BufferedReader metaReader = null;
        try {
            metaReader = new BufferedReader(new InputStreamReader(new FileInputStream(metaFile)));
            return Integer.parseInt(metaReader.readLine());
        } catch (Exception e) {
            return 0;
        } finally {
            if (metaReader != null) {
                try {
                    metaReader.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private String extractContent(Article article, Part part) throws Exception {
        String content = null;

        int tryCount = 1;
        while (true) {
            try {
                Document doc = Jsoup.connect(part.url).referrer(article.href).userAgent(userAgent).timeout(5000).get();
                Elements divs = doc.select("div.ART");

                StringBuilder builder = new StringBuilder();
                for (Element div : divs) {
                    builder.append(div.html().replace("<br>", "\r\n"));
                }

                content = builder.toString();

                break;
            } catch (Exception e) {
                tryCount++;
                if (tryCount > 3) {
                    throw e;
                }
            }
        }

        BufferedWriter writer = null;
        try {
            File partFile = new File(article.file.getParentFile(), part.index + ".txt");
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(partFile), "utf-8"));
            writer.write(content);
            writer.flush();
        } catch (Exception e) {
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                }
            }
        }

        return content;
    }

}
