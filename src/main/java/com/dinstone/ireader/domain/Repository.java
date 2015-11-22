
package com.dinstone.ireader.domain;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Repository {

    public List<Category> categorys;

    public Map<String, Category> categoryMap;

    public Map<String, Article> articleMap;

    public Category topCategory = new Category("111", "排行榜");

    public Date updateTime = new Date();

}
