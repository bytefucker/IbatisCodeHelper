package com.ccnode.codegenerator.dialog.dto.mybatis;

import com.intellij.psi.xml.XmlTag;

/**
 * Created by bruce.ge on 2016/12/27.
 */
public class MapperSql {
    private String id;

    private XmlTag tag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public XmlTag getTag() {
        return tag;
    }

    public void setTag(XmlTag tag) {
        this.tag = tag;
    }
}
