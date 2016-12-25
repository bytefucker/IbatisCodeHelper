package com.ccnode.codegenerator.dialog;

/**
 * Created by bruce.ge on 2016/12/25.
 */
public class GenCodeProp {

    private String fieldName;

    private String columnName;

    private Boolean primaryKey;

    private Boolean unique;

    private String size;

    private String filedType;

    private String defaultValue;

    private Boolean canBeNull;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Boolean getCanBeNull() {
        return canBeNull;
    }

    public void setCanBeNull(Boolean canBeNull) {
        this.canBeNull = canBeNull;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Boolean getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(Boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Boolean getUnique() {
        return unique;
    }

    public void setUnique(Boolean unique) {
        this.unique = unique;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getFiledType() {
        return filedType;
    }

    public void setFiledType(String filedType) {
        this.filedType = filedType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
