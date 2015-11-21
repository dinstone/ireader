
package com.dinstone.ireader.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.dinstone.ireader.domain.Article;
import com.dinstone.ireader.domain.Category;
import com.dinstone.ireader.domain.Pagenation;
import com.dinstone.ireader.domain.Part;
import com.dinstone.ireader.domain.Repository;
import com.dinstone.ireader.service.SynchronizeService;

@Service
@RequestMapping(value = "/article")
public class ArticleController {

    @Resource
    private SynchronizeService synchronizeService;

    @RequestMapping(value = "/list/{pageNumber}")
    public ModelAndView list(@PathVariable int pageNumber) {
        Category category = Repository.getInstance().topCategory;
        return create(category, pageNumber, "list");
    }

    @RequestMapping(value = "/category/{categoryId}-{pageNumber}")
    public ModelAndView category(@PathVariable String categoryId, @PathVariable int pageNumber) {
        Map<String, Category> categorys = Repository.getInstance().categoryMap;
        Category category = categorys.get(categoryId);
        if (category == null) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("message", "未知的文章类型.");
            return mav;
        }

        return create(category, pageNumber, "category");
    }

    private ModelAndView create(Category category, int pageNumber, String viewName) {
        ModelAndView mav = new ModelAndView(viewName);
        mav.addObject("categorys", Repository.getInstance().categorys);
        mav.addObject("category", category);

        if (pageNumber <= 0) {
            pageNumber = 1;
        }

        List<Pagenation> pages = category.pages;
        if (pageNumber <= pages.size()) {
            Map<String, Object> pagenation = new HashMap<String, Object>();
            pagenation.put("current", pageNumber);
            pagenation.put("total", pages.size());
            if (pageNumber > 1) {
                pagenation.put("prev", pageNumber - 1);
            }

            if (pageNumber < pages.size()) {
                pagenation.put("next", pageNumber + 1);
            }

            mav.addObject("articles", pages.get(pageNumber - 1).articles);
            mav.addObject("pagenation", pagenation);
        }
        return mav;
    }

    @RequestMapping("/read/{articleId}-{partIndex}")
    public ModelAndView read(@PathVariable String articleId, @PathVariable int partIndex) throws Exception {
        Article article = synchronizeService.findAticle(articleId);
        if (article == null) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("message", "文章正在更新请稍后再试.");
            return mav;
        }

        ModelAndView mav = new ModelAndView("read");
        mav.addObject("categorys", Repository.getInstance().categorys);
        mav.addObject("article", article);

        if (partIndex <= 0) {
            partIndex = 1;
        }

        Part[] parts = article.parts;
        if (parts != null && partIndex <= parts.length) {
            Map<String, Object> pagenation = new HashMap<String, Object>();
            pagenation.put("current", partIndex);
            pagenation.put("total", parts.length);
            if (partIndex > 1) {
                pagenation.put("prev", partIndex - 1);
            }

            if (partIndex < parts.length) {
                pagenation.put("next", partIndex + 1);
            }

            StringBuilder content = new StringBuilder();

            Part part = parts[partIndex - 1];
            File partFile = new File(article.file.getParentFile(), part.index + ".txt");
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(partFile), "utf-8"));
                String temp = null;
                while ((temp = reader.readLine()) != null) {
                    content.append(temp);
                    content.append("<br>");
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

            mav.addObject("part", part);
            mav.addObject("content", content.toString());
            mav.addObject("pagenation", pagenation);
        }

        return mav;
    }

    @RequestMapping("/directory/{articleId}")
    public ModelAndView directory(@PathVariable String articleId) throws Exception {
        Article article = synchronizeService.findAticle(articleId);
        if (article == null) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("message", "文章正在更新请稍后再试.");
            return mav;
        }

        ModelAndView mav = new ModelAndView("directory");
        mav.addObject("categorys", Repository.getInstance().categorys);
        mav.addObject("article", article);

        return mav;
    }

    @RequestMapping("/download/{articleId}")
    public ModelAndView download(@PathVariable String articleId, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Article article = synchronizeService.findAticle(articleId);
        if (article == null) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("message", "文章正在更新请稍后再试.");
            return mav;
        }

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            File artFile = article.file;
            response.setHeader("Content-Length", String.valueOf(artFile.length()));

            response.setContentType("application/x-msdownload;");

            String downloadName = new String(article.name.getBytes("utf-8"), "ISO8859-1");
            response.setHeader("Content-disposition", "attachment; filename=" + downloadName + ".txt");

            bis = new BufferedInputStream(new FileInputStream(artFile));
            bos = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } finally {
            if (bis != null)
                bis.close();
            if (bos != null)
                bos.close();
        }

        return null;
    }
}