
package com.dinstone.ireader.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Repository {

    public TreeMap<String, Category> categoryMap = new TreeMap<String, Category>();

    public Map<String, Article> articleMap = new HashMap<String, Article>();

}
