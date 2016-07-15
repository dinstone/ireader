
package com.dinstone.ireader.domain;

/**
 * 状态
 * 
 * @author dinstone
 * @version 1.0.0
 */
public enum Status {
    PROGRESS("连载中"), FINISH("全文完");

    private String label;

    private Status(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

    public static Status value(String label) {
        if ("连载中".equals(label)) {
            return PROGRESS;
        } else if ("全文完".equals(label)) {
            return FINISH;
        }
        throw new IllegalArgumentException("unsupported status [" + label + "]");
    }
}
