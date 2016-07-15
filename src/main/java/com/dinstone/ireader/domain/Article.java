
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

    public String author;

    public String status;

    public String href;

    public int read;

    public Part[] parts;

    @JsonIgnore
    public Category category;

    @JsonIgnore
    public File file;

    @JsonIgnore
    public volatile boolean proccess;

    @JsonIgnore
    public volatile Date update;

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
        return author;
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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Article other = (Article) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Article [id=" + id + ", name=" + name + ", author=" + author + ", status=" + status + ", read=" + read
                + "]";
    }

    @Override
    public int compareTo(Article other) {
        if (this.equals(other)) {
            return 0;
        }

        return other.read - this.read;
    }

}
