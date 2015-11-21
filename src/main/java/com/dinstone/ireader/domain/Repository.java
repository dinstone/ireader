
package com.dinstone.ireader.domain;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.dinstone.ireader.service.JacksonSerializer;

public class Repository implements Serializable {

    /**  */
    private static final long serialVersionUID = 1L;

    private static Repository instance = new Repository();

    public static Repository getInstance() {
        return instance;
    }

    private transient JacksonSerializer serializer = new JacksonSerializer();

    public List<Article> articles;

    public List<Category> categorys;

    public Map<String, Category> categoryMap = new HashMap<String, Category>();

    public Map<String, Article> articlesMap = new HashMap<String, Article>();

    public Category topCategory = new Category("111", "排行榜");

    public Date updateTime = new Date();

    private void writeArticles() throws IOException {
        BufferedOutputStream writer = null;
        try {
            File contentFile = new File("logs", "repository.data");
            if (!contentFile.exists()) {
                contentFile.getParentFile().mkdirs();
            }

            writer = new BufferedOutputStream(new FileOutputStream(contentFile, true));

            if (articles != null) {
                for (Article article : articles) {
                    byte[] bytes = serializer.serialize(article);
                    writer.write(bytes);
                    writer.write('\r');
                    writer.write('\n');
                    writer.flush();
                }
            }
            System.out.println("同步完成");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private void loadArticles() throws Exception {
        List<Article> articles = new LinkedList<Article>();
        File metaFile = new File("logs", "repository.data");
        if (metaFile.exists()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(metaFile), "utf-8"));
                String temp = null;
                while ((temp = reader.readLine()) != null) {
                    Article article = serializer.deserialize(temp.getBytes("utf-8"), Article.class);
                    articles.add(article);
                }
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                    }
                }
            }
        }

        if (articles.size() > 0) {
            this.articles = articles;
        }
    }

    public static int getIndex(File metaFile) throws FileNotFoundException, IOException {
        BufferedReader metaReader = null;
        try {
            metaReader = new BufferedReader(new InputStreamReader(new FileInputStream(metaFile)));

            return Integer.parseInt(metaReader.readLine());
        } finally {
            if (metaReader != null) {
                try {
                    metaReader.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Repository repository = Repository.getInstance();
        // repository.init();

        System.out.println("ok");

        System.in.read();
    }

}
