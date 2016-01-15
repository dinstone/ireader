
package com.dinstone.ireader.domain;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 文章
 * 
 * @author dinstone
 * @version 1.0.0
 */
public class Article implements Serializable, Comparable<Article> {

    /**  */
    private static final long serialVersionUID = 1L;

    public String id;

    public String name;

    @JsonIgnore
    public Category category;

    public String auth;

    public String status;

    public String href;

    public File file;

    public volatile boolean proccess;

    public volatile Date update;

    public Part[] parts;

    public Article() {
        super();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAuth() {
        return auth;
    }

    public Category getCategory() {
        return category;
    }

    public String getStatus() {
        return status;
    }

    public String getHref() {
        return href;
    }

    public Date getUpdate() {
        return update;
    }

    public Part[] getParts() {
        return parts;
    }

    @Override
    public String toString() {
        return "Article [id=" + id + ", name=" + name + ", auth=" + auth + ", category=" + category + ", status="
                + status + "]";
    }

    @Override
    public int compareTo(Article other) {
        if (other == null) {
            return -1;
        }

        if (this == other) {
            return 0;
        }
        return this.id.compareTo(other.id);
    }

}
