
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

    public static int page_size = 50;

    public int total;

    public int prev;

    public int current;

    public int next;

    public int sIndex;

    public int eIndex;

    public List<Article> articles;

    public Pagenation(int totalSize, int pageNumber) {
        // 计算总页数
        if (totalSize < 0) {
            totalSize = 0;
        }
        total = totalSize / page_size;
        if (totalSize % page_size != 0) {
            total++;
        }
        // 初始化当前页数
        if (pageNumber <= 0) {
            pageNumber = 1;
        } else if (pageNumber > total && total > 0) {
            pageNumber = total;
        }
        current = pageNumber;
        // 初始化前一页后一页
        if (current > 1) {
            prev = current - 1;
        }
        if (current < total) {
            next = current + 1;
        }

        // 计算文章开始结束索引
        sIndex = (current - 1) * page_size;
        eIndex = Math.min(current * page_size, totalSize);
    }

    public int getTotal() {
        return total;
    }

    public int getPrev() {
        return prev;
    }

    public int getCurrent() {
        return current;
    }

    public int getNext() {
        return next;
    }

    public int getsIndex() {
        return sIndex;
    }

    public int geteIndex() {
        return eIndex;
    }

    public List<Article> getArticles() {
        return articles;
    }

}
