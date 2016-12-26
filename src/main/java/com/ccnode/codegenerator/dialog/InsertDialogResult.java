package com.ccnode.codegenerator.dialog;

import com.ccnode.codegenerator.pojo.ClassInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by bruce.ge on 2016/12/25.
 */
public class InsertDialogResult {
    private List<GenCodeProp> propList;

    private String primaryKey;

    private Map<InsertFileType,InsertFileProp> fileProps;

    private String tableName;

    private ClassInfo srcClass;

    public ClassInfo getSrcClass() {
        return srcClass;
    }

    public void setSrcClass(ClassInfo srcClass) {
        this.srcClass = srcClass;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<GenCodeProp> getPropList() {
        return propList;
    }

    public void setPropList(List<GenCodeProp> propList) {
        this.propList = propList;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Map<InsertFileType, InsertFileProp> getFileProps() {
        return fileProps;
    }

    public void setFileProps(Map<InsertFileType, InsertFileProp> fileProps) {
        this.fileProps = fileProps;
    }
}
