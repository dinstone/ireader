
package com.dinstone.ireader.facade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dinstone.ireader.domain.Article;
import com.dinstone.ireader.domain.Category;
import com.dinstone.ireader.domain.Pagenation;
import com.dinstone.ireader.service.RepositoryService;

@RestController
@RequestMapping("/category")
public class CategoryResource {

    @Autowired
    private RepositoryService repositoryService;

    @GetMapping("/list")
    public List<Map<String, String>> list() {
        List<Map<String, String>> clist = new ArrayList<Map<String, String>>();
        Collection<Category> categorys = repositoryService.getRepository().categoryMap.values();
        if (categorys != null) {
            for (Category category : categorys) {
                HashMap<String, String> cat = new HashMap<String, String>();
                cat.put("id", category.getId());
                cat.put("name", category.getName());
                clist.add(cat);
            }
        }

        return clist;
    }

    @GetMapping("/{categoryId}/{pageNumber}")
    public Map<String, Object> categoryPage(@PathVariable("categoryId") String categoryId,
            @PathVariable("pageNumber") int pageNumber) {
        Map<String, Object> result = new HashMap<String, Object>();

        Map<String, Category> categorys = repositoryService.getRepository().categoryMap;
        Category category = categorys.get(categoryId);
        if (category == null) {
            return result;
        }

        Pagenation pagenation = category.getPageArticles(pageNumber);

        Map<String, Object> pagenationMap = new HashMap<String, Object>();
        pagenationMap.put("current", pagenation.current);
        pagenationMap.put("total", pagenation.total);
        if (pagenation.current > 1) {
            pagenationMap.put("prev", pagenation.prev);
        }
        if (pagenation.current < pagenation.total) {
            pagenationMap.put("next", pagenation.next);
        }

        List<Map<String, Object>> aml = new LinkedList<Map<String, Object>>();
        for (Article article : pagenation.articles) {
            Map<String, Object> am = new HashMap<String, Object>();
            am.put("id", article.id);
            am.put("name", article.name);
            am.put("status", article.status);
            aml.add(am);
        }
        result.put("articles", aml);
        result.put("category", category.name);
        result.put("pagenation", pagenation);

        return result;
    }
}
