
package com.dinstone.ireader.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 分页
 * 
 * @author dinstone
 * @version 1.0.0
 */
public class Pagenation implements Serializable {

    /**  */
    private static final long serialVersionUID = 1L;

    public int index;

    public String access;

    public String next;

    public List<Article> articles;

    public int getIndex() {
        return index;
    }

    public String getAccess() {
        return access;
    }

    public String getNext() {
        return next;
    }

    public List<Article> getArticles() {
        return articles;
    }

}
