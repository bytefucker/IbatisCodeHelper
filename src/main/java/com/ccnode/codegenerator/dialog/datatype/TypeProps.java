package com.ccnode.codegenerator.dialog.datatype;

/**
 * Created by bruce.ge on 2016/12/25.
 */
public class TypeProps {
    private String defaultType;

    private String size;

    private Boolean canBeNull;

    private Boolean unique;

    private String defaultValue;

    private Boolean primary;


    public Boolean getPrimary() {
        return primary;
    }

    public void setPrimary(Boolean primary) {
        this.primary = primary;
    }

    public String getDefaultType() {
        return defaultType;
    }

    public void setDefaultType(String defaultType) {
        this.defaultType = defaultType;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Boolean getCanBeNull() {
        return canBeNull;
    }

    public void setCanBeNull(Boolean canBeNull) {
        this.canBeNull = canBeNull;
    }

    public Boolean getUnique() {
        return unique;
    }

    public void setUnique(Boolean unique) {
        this.unique = unique;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }


    public TypeProps(String defaultType, String size, String defaultValue) {
        this.defaultType = defaultType;
        this.size = size;
        this.defaultValue = defaultValue;
        this.canBeNull = false;
        this.unique = false;
        this.primary = false;
    }
}
