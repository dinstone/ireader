
package com.dinstone.ireader.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

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

    @JsonIgnore
    public Map<String, Article> articleMap = new ConcurrentHashMap<String, Article>();

    public List<Pagenation> pages = new CopyOnWriteArrayList<Pagenation>();

    public Category() {
        super();
    }

    public Category(String id, String name) {
        this.id = id;
        this.name = name;
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

    public List<Pagenation> getPages() {
        return pages;
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

}
