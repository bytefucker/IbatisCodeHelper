package com.ccnode.codegenerator.dialog.dto.mybatis;

import com.intellij.psi.xml.XmlTag;

/**
 * Created by bruce.ge on 2016/12/27.
 */
public class MapperMethod {
    private MapperMethodEnum type;

    private XmlTag xmlTag;

    public MapperMethodEnum getType() {
        return type;
    }

    public void setType(MapperMethodEnum type) {
        this.type = type;
    }

    public XmlTag getXmlTag() {
        return xmlTag;
    }

    public void setXmlTag(XmlTag xmlTag) {
        this.xmlTag = xmlTag;
    }
}
