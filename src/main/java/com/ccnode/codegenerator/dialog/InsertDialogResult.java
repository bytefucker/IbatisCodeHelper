package com.ccnode.codegenerator.dialog;

import java.util.List;

/**
 * Created by bruce.ge on 2016/12/25.
 */
public class InsertDialogResult {
    private List<GenCodeProp> propList;

    private String primaryKey;

    private List<InsertFileProp> fileProps;

    private String tableName;

    private String daoPackageName;

    private String servicePackageName;


    public String getDaoPackageName() {
        return daoPackageName;
    }

    public void setDaoPackageName(String daoPackageName) {
        this.daoPackageName = daoPackageName;
    }

    public String getServicePackageName() {
        return servicePackageName;
    }

    public void setServicePackageName(String servicePackageName) {
        this.servicePackageName = servicePackageName;
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

    public List<InsertFileProp> getFileProps() {
        return fileProps;
    }

    public void setFileProps(List<InsertFileProp> fileProps) {
        this.fileProps = fileProps;
    }
}
