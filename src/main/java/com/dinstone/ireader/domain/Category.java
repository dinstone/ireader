
package com.dinstone.ireader.domain;

import java.io.Serializable;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

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

    public SortedSet<Article> articleSet = new TreeSet<Article>();

    public Category() {
        super();
    }

    public Category(String id, String name, boolean persistent) {
        this.id = id;
        this.name = name;
        this.persistent = persistent;
    }

    @Override
    public String toString() {
        return "Category [id=" + id + ", name=" + name + "]";
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getHref() {
        return href;
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
        Article[] articles = articleSet.toArray(new Article[0]);
        Pagenation pagenation = new Pagenation(articleSet.size(), pageNumber);
        pagenation.articles = Arrays.copyOfRange(articles, pagenation.sIndex, pagenation.eIndex);
        return pagenation;
    }
}
