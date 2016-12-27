package com.ccnode.codegenerator.dialog.dto;

import com.ccnode.codegenerator.dialog.dto.mybatis.MapperMethod;
import com.ccnode.codegenerator.dialog.dto.mybatis.MapperSql;
import com.ccnode.codegenerator.dialog.dto.mybatis.ResultMap;

import java.util.List;
import java.util.Map;

/**
 * Created by bruce.ge on 2016/12/27.
 */
public class MapperDto {
    private List<ResultMap> resultMapList;

    private List<MapperSql> sqls;

    private Map<String, MapperMethod> mapperMethodMap;


    public List<ResultMap> getResultMapList() {
        return resultMapList;
    }

    public void setResultMapList(List<ResultMap> resultMapList) {
        this.resultMapList = resultMapList;
    }

    public List<MapperSql> getSqls() {
        return sqls;
    }

    public void setSqls(List<MapperSql> sqls) {
        this.sqls = sqls;
    }

    public Map<String, MapperMethod> getMapperMethodMap() {
        return mapperMethodMap;
    }

    public void setMapperMethodMap(Map<String, MapperMethod> mapperMethodMap) {
        this.mapperMethodMap = mapperMethodMap;
    }
}
