
package com.dinstone.ireader.domain;

/**
 * 章节
 * 
 * @author dinstone
 * @version 1.0.0
 */
public class Part implements Comparable<Part> {

    public int index;

    public String name;

    public String url;

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int compareTo(Part other) {
        return this.index - other.index;
    }

}
