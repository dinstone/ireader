
package com.dinstone.ireader.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
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

    private static String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.154 Safari/537.36";

    private Article article;

    private File articleDir;

    public ArticleUpdater(Article article, Configuration config) {
        this.article = article;
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
            article.parts = updateParts(article);
        } catch (Exception e) {
            LOG.error("更新文章[{}]章节失败 ", article.name);
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

                    String[] contents = extractContents(article, part);

                    writer.write(contents[0]);
                    writer.newLine();
                    writer.write(contents[1]);
                    writer.newLine();
                    writer.flush();
                }
            }

            article.update = new Date();
            LOG.info("同步文章[{}]完成: {}", article.name, articleFile);
        } catch (Exception e) {
            LOG.warn("同步文章[{}]第{}节时失败", article.name, index);
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

    private String[] extractContents(Article article, Part part) throws Exception {
        String[] contents = new String[2];

        int tryCount = 1;
        while (true) {
            try {
                Document doc = Jsoup.connect(part.url).userAgent(userAgent).timeout(5000).get();
                Elements divs = doc.select("div.ART");

                StringBuilder builder = new StringBuilder();
                for (Element div : divs) {
                    builder.append(div.html().replace("<br>", "\r\n"));
                }

                contents[0] = doc.title();
                contents[1] = builder.toString();

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
            writer.write(contents[1]);
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

        return contents;
    }

    private Part[] updateParts(Article article) throws IOException {
        String url = article.href;
        int tryCount = 1;
        while (true) {
            try {
                Document doc = Jsoup.connect(url).userAgent(userAgent).timeout(5000).get();
                // extract auth,category,status
                Elements bases = doc.select("span.TA");
                for (Element base : bases) {
                    // 作者: 御风楼主人 　 分类: 鬼话 　 [全文完]
                    String[] bp = base.text().trim().replaceAll("　", "").split(" +");
                    if (bp.length >= 5) {
                        article.auth = bp[1];
                        article.status = bp[4];
                    }
                }

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
                        part.name = name;
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

}
