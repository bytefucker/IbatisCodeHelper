package com.ccnode.codegenerator.dialog;

/**
 * Created by bruce.ge on 2016/12/25.
 */
public class InsertFileProp {

    //remove with .java ect.
    private String name;

    private String folderPath;

    private String packageName;

    private String fullPath;

    private String qutifiedName;


    public String getQutifiedName() {
        return qutifiedName;
    }

    public void setQutifiedName(String qutifiedName) {
        this.qutifiedName = qutifiedName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }


    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }
}
