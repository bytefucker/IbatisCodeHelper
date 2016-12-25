package com.ccnode.codegenerator.dialog.datatype;

/**
 * Created by bruce.ge on 2016/12/25.
 */
public class TypeDefault {
    private String size;

    private String defaultValue;

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public TypeDefault(String size, String defaultValue) {
        this.size = size;
        this.defaultValue = defaultValue;
    }
}
