
package com.dinstone.ireader.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Service;

import com.dinstone.ireader.domain.Article;
import com.dinstone.ireader.domain.Category;
import com.dinstone.ireader.domain.Pagenation;
import com.dinstone.ireader.service.RepositoryManager;

@Service
@Path("/app")
@Produces(MediaType.APPLICATION_JSON)
public class AppResource {

    @GET
    @Path("/patch/{appVersion}/{patchVersion}")
    public List<Map<String, String>> patch(@PathParam("appVersion") String appVersion,
            @PathParam("patchVersion") String patchVersion) {
        List<Map<String, String>> patchList = new ArrayList<Map<String, String>>();

        int pv = 0;
        if (patchVersion != null) {
            try {
                pv = Integer.parseInt(patchVersion);
            } catch (NumberFormatException e) {
                // ignore;
            }
        }

        for (int i = 1; i < 5; i++) {
            if (pv < i) {
                HashMap<String, String> patchInfoMap = new HashMap<String, String>();
                patchInfoMap.put("appVersionName", appVersion);
                patchInfoMap.put("patchVersionName", "" + i);
                patchInfoMap.put("patchFileUrl", "" + (pv + i));
                patchInfoMap.put("patchFileMd5", "" + (pv + i));

                patchList.add(patchInfoMap);
            }
        }

        return patchList;
    }

    @GET
    @Path("/top/{pageNumber}")
    public Map<String, Object> topPage(@PathParam("pageNumber") int pageNumber) {
        Map<String, Object> result = new HashMap<String, Object>();

        Category category = RepositoryManager.getInstance().getRepository().topCategory;

        List<Pagenation> pages = category.pages;
        Map<String, Object> pagenation = new HashMap<String, Object>();

        if (pageNumber <= 0) {
            pageNumber = 1;
        }
        if (pageNumber <= pages.size()) {
            pagenation.put("current", pageNumber);
            pagenation.put("total", pages.size());
            if (pageNumber > 1) {
                pagenation.put("prev", pageNumber - 1);
            }

            if (pageNumber < pages.size()) {
                pagenation.put("next", pageNumber + 1);
            }

            List<Map<String, Object>> aml = new ArrayList<Map<String, Object>>();
            List<Article> articles = pages.get(pageNumber - 1).articles;
            for (Article article : articles) {
                Map<String, Object> am = new HashMap<String, Object>();
                am.put("id", article.id);
                am.put("name", article.name);
                am.put("status", article.status);
                am.put("category", article.category.name);
                aml.add(am);
            }
            result.put("articles", aml);
            result.put("pagenation", pagenation);
        }

        return result;
    }

    @GET
    @Path("/{categoryId}/{pageNumber}")
    public Map<String, Object> categoryPage(@PathParam("categoryId") String categoryId,
            @PathParam("pageNumber") int pageNumber) {
        Map<String, Object> result = new HashMap<String, Object>();

        Map<String, Category> categorys = RepositoryManager.getInstance().getRepository().categoryMap;
        Category category = categorys.get(categoryId);
        if (category == null) {
            return result;
        }

        List<Pagenation> pages = category.pages;
        Map<String, Object> pagenation = new HashMap<String, Object>();

        if (pageNumber <= 0) {
            pageNumber = 1;
        }
        if (pageNumber <= pages.size()) {
            pagenation.put("current", pageNumber);
            pagenation.put("total", pages.size());
            if (pageNumber > 1) {
                pagenation.put("prev", pageNumber - 1);
            }

            if (pageNumber < pages.size()) {
                pagenation.put("next", pageNumber + 1);
            }

            List<Map<String, Object>> aml = new ArrayList<Map<String, Object>>();
            List<Article> articles = pages.get(pageNumber - 1).articles;
            for (Article article : articles) {
                Map<String, Object> am = new HashMap<String, Object>();
                am.put("id", article.id);
                am.put("name", article.name);
                am.put("status", article.status);
                aml.add(am);
            }
            result.put("category", category.name);
            result.put("articles", aml);
            result.put("pagenation", pagenation);
        }

        return result;
    }
}
