
package com.dinstone.ireader.facade;

import java.util.ArrayList;
import java.util.Collection;
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
@Path("/category")
@Produces(MediaType.APPLICATION_JSON)
public class CategoryResource {

    @GET
    @Path("/list")
    public List<Map<String, String>> list() {
        List<Map<String, String>> clist = new ArrayList<Map<String, String>>();
        Collection<Category> categorys = RepositoryManager.getInstance().getRepository().categorys;
        if (categorys != null) {
            for (Category category : categorys) {
                HashMap<String, String> cat = new HashMap<String, String>();
                cat.put("id", category.getId());
                cat.put("name", category.getName());
                cat.put("pages", "" + category.getPages().size());
                clist.add(cat);
            }
        }

        return clist;
    }

    @GET
    @Path("/{categoryId}/{pageNumber}")
    public Map<String, Object> page(@PathParam("categoryId") String categoryId, @PathParam("pageNumber") int pageNumber) {
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
                am.put("state", article.status);
                aml.add(am);
            }
            result.put("articles", aml);
            result.put("pagenation", pagenation);
        }

        return result;
    }
}
