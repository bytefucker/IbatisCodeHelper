package com.ccnode.codegenerator.dialog.dto.mybatis;

/**
 * Created by bruce.ge on 2016/12/28.
 */
public class ClassMapperMethod {

    //now only support with one as param . not support such as update(@Param("pojo")User user,@Param("nima")User updatedUser);
    private String methodName;

    private String paramAnno;

    private boolean list;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getParamAnno() {
        return paramAnno;
    }

    public void setParamAnno(String paramAnno) {
        this.paramAnno = paramAnno;
    }

    public boolean getList() {
        return list;
    }

    public void setList(Boolean list) {
        this.list = list;
    }
}
