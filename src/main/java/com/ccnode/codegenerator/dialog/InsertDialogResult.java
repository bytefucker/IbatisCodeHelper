package com.ccnode.codegenerator.dialog;

import com.ccnode.codegenerator.pojo.ClassInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by bruce.ge on 2016/12/25.
 */
public class InsertDialogResult {
    private List<GenCodeProp> propList;

    private GenCodeProp primaryProp;
    private Map<InsertFileType, InsertFileProp> fileProps;

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

    public GenCodeProp getPrimaryProp() {
        return primaryProp;
    }

    public void setPrimaryProp(GenCodeProp primaryProp) {
        this.primaryProp = primaryProp;
    }

    public Map<InsertFileType, InsertFileProp> getFileProps() {
        return fileProps;
    }

    public void setFileProps(Map<InsertFileType, InsertFileProp> fileProps) {
        this.fileProps = fileProps;
    }
}
