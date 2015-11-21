
package com.dinstone.ireader.domain;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

/**
 * 文章
 * 
 * @author dinstone
 * @version 1.0.0
 */
public class Article implements Serializable {

    /**  */
    private static final long serialVersionUID = 1L;

    public String id;

    public String name;

    public String auth;

    public String category;

    public String status;

    public String href;

    public File file;

    public boolean proccess;

    public volatile Date update;

    public Part[] parts;

    public Article() {
        super();
    }

    @Override
    public String toString() {
        return "Article [id=" + id + ", name=" + name + ", auth=" + auth + ", category=" + category + ", status="
                + status + "]";
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

    public String getCategory() {
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

}
