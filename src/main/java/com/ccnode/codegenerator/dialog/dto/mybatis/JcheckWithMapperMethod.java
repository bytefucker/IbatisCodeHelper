package com.ccnode.codegenerator.dialog.dto.mybatis;

import javax.swing.*;

/**
 * Created by bruce.ge on 2016/12/27.
 */
public class JcheckWithMapperMethod {
    private JCheckBox jCheckBox;

    private MapperMethod mapperMethod;

    private ClassMapperMethod classMapperMethod;


    public ClassMapperMethod getClassMapperMethod() {
        return classMapperMethod;
    }

    public void setClassMapperMethod(ClassMapperMethod classMapperMethod) {
        this.classMapperMethod = classMapperMethod;
    }

    public JCheckBox getjCheckBox() {
        return jCheckBox;
    }

    public void setjCheckBox(JCheckBox jCheckBox) {
        this.jCheckBox = jCheckBox;
    }

    public MapperMethod getMapperMethod() {
        return mapperMethod;
    }

    public void setMapperMethod(MapperMethod mapperMethod) {
        this.mapperMethod = mapperMethod;
    }
}
