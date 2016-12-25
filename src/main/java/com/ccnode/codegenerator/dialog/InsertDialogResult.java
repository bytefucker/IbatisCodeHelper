package com.ccnode.codegenerator.dialog;

import java.util.List;

/**
 * Created by bruce.ge on 2016/12/25.
 */
public class InsertDialogResult {
    private List<GenCodeProp> propList;

    private String primaryKey;

    private List<InsertFileProp> fileProps;

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

    public List<InsertFileProp> getFileProps() {
        return fileProps;
    }

    public void setFileProps(List<InsertFileProp> fileProps) {
        this.fileProps = fileProps;
    }
}
