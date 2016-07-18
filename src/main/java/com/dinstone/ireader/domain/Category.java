
package com.dinstone.ireader.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 分类
 * 
 * @author dinstone
 * @version 1.0.0
 */
public class Category implements Comparable<Category>, Serializable {

    /**  */
    private static final long serialVersionUID = 1L;

    public String id;

    public String name;

    public String href;

    public boolean persistent = true;

    @JsonIgnore
    public List<Article> articleList = new LinkedList<Article>();

    @JsonIgnore
    public Map<String, Article> articleMap = new HashMap<String, Article>();

    public Category() {
        super();
    }

    public Category(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Category(String id, String name, boolean persistent) {
        this.id = id;
        this.name = name;
        this.persistent = persistent;
    }

    public Category(String id, String name, String href) {
        this.id = id;
        this.name = name;
        this.href = href;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Category [id=" + id + ", name=" + name + "]";
    }

    @Override
    public int compareTo(Category other) {
        if (other == null) {
            return -1;
        }

        if (this == other) {
            return 0;
        }
        return this.id.compareTo(other.id);
    }

    public Pagenation getPageArticles(int pageNumber) {
        Pagenation pagenation = new Pagenation(articleList.size(), pageNumber);
        pagenation.articles = articleList.subList(pagenation.sIndex, pagenation.eIndex);
        return pagenation;
    }

    // public void sortArticles() {
    // Article[] articles = articleList.toArray(new Article[0]);
    // Arrays.sort(articles);
    // this.sortedArticles = articles;
    // }

    public boolean addArticle(Article article) {
        if (getArticle(article.id) == null) {
            articleMap.put(article.id, article);
            articleList.add(article);
            return true;
        }

        return false;
    }

    public Article getArticle(String articleId) {
        return articleMap.get(articleId);
    }

}
