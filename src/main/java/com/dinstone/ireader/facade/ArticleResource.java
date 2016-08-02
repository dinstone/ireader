
package com.dinstone.ireader.facade;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Service;

import com.dinstone.ireader.domain.Article;
import com.dinstone.ireader.domain.Part;
import com.dinstone.ireader.domain.Repository;
import com.dinstone.ireader.service.ArticleService;
import com.dinstone.ireader.service.RepositoryManager;

@Service
@Path("/article")
@Produces(MediaType.APPLICATION_JSON)
public class ArticleResource {

    @Resource
    private RepositoryManager repositoryManager;

    @Resource
    private ArticleService articleService;

    @GET
    @Path("/query")
    public List<Map<String, String>> query(@QueryParam("kw") String keyword) {
        Repository repository = repositoryManager.getRepository();
        Collection<Article> articles = repository.articleMap.values();

        List<Map<String, String>> aml = new LinkedList<Map<String, String>>();
        for (Article article : articles) {
            if (article.name != null && article.name.contains(keyword)) {
                Map<String, String> am = new HashMap<String, String>();
                am.put("id", article.id);
                am.put("name", article.name);
                am.put("author", article.author);
                am.put("status", article.status);
                am.put("category", article.category.name);
                aml.add(am);
            }
        }

        return aml;
    }

    @GET
    @Path("/directory/{articleId}")
    public Map<String, Object> directory(@PathParam("articleId") String articleId) {
        Repository repository = repositoryManager.getRepository();
        Article article = articleService.findAticle(repository, articleId);
        if (article == null) {
            throw new IllegalStateException("文章正在更新，请稍后再试.");
        }

        Map<String, Object> am = new HashMap<String, Object>();
        am.put("id", article.id);
        am.put("name", article.name);
        am.put("author", article.author);
        am.put("status", article.status);
        am.put("category", article.category.name);

        Part[] parts = article.parts;
        if (parts != null) {
            List<Integer> partIndexs = new LinkedList<Integer>();
            for (Part part : parts) {
                partIndexs.add(part.index);
            }
            am.put("partIndexs", partIndexs);
        }

        return am;
    }

    @GET
    @Path("/content/{articleId}/{partIndex}")
    public Map<String, Object> content(@PathParam("articleId") String articleId, @PathParam("partIndex") int partIndex) {
        Repository repository = repositoryManager.getRepository();
        Article article = articleService.findAticle(repository, articleId);
        if (article == null) {
            throw new IllegalStateException("文章正在更新，请稍后再试.");
        }

        if (partIndex <= 0) {
            partIndex = 1;
        }

        Part[] parts = article.parts;
        if (partIndex > parts.length) {
            partIndex = parts.length;
        }

        Map<String, Object> mav = new HashMap<String, Object>();

        Map<String, Object> navigation = new HashMap<String, Object>();
        navigation.put("current", partIndex);
        navigation.put("total", parts.length);
        if (partIndex > 1) {
            navigation.put("prev", partIndex - 1);
        }
        if (partIndex < parts.length) {
            navigation.put("next", partIndex + 1);
        }
        mav.put("navigation", navigation);

        Part part = parts[partIndex - 1];
        String content = loadPartContent(article, part);

        mav.put("partIndex", partIndex);
        mav.put("content", content);

        return mav;
    }

    protected String loadPartContent(Article article, Part part) {
        StringBuilder content = new StringBuilder();
        BufferedReader reader = null;
        try {
            File partFile = articleService.getPartFile(article, part);
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(partFile), "utf-8"));
            String temp = null;
            while ((temp = reader.readLine()) != null) {
                content.append(temp);
                content.append("\r\n");
            }
        } catch (Exception e) {
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }

        return content.toString();
    }
}
