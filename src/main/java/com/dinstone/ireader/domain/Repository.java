
package com.dinstone.ireader.domain;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Repository {

    public Collection<Category> categorys;

    public Map<String, Category> categoryMap = new ConcurrentHashMap<String, Category>();

    public Map<String, Article> articleMap = new ConcurrentHashMap<String, Article>();

    public Category topCategory = new Category("111", "排行榜");

    public Date updateTime = new Date();

}
