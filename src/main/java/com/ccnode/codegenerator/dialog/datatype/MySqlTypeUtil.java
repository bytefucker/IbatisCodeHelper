package com.ccnode.codegenerator.dialog.datatype;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bruce.ge on 2016/12/25.
 */
public class MySqlTypeUtil {

    private static Map<String, TypeProps> javaDefaultMap = new HashMap<>();

    private static Map<String, String[]> javaRecommendMap = new HashMap<>();


    static {

        TypeProps intTypeProp = new TypeProps(MysqlTypeConstants.INTEGER, "11", false, false, "-1");
        javaDefaultMap.put("int", intTypeProp);
        javaDefaultMap.put("java.lang.Integer", intTypeProp);

        TypeProps longDefault = new TypeProps(MysqlTypeConstants.BIGINT, "15", false, false, "-1");
        javaDefaultMap.put("long", longDefault);
        javaDefaultMap.put("java.lang.Long", longDefault);

        TypeProps floatTypeProp = new TypeProps(MysqlTypeConstants.FLOAT, "15", false, false, "-1.0");

        javaDefaultMap.put("float", floatTypeProp);
        javaDefaultMap.put("java.lang.FLoat", floatTypeProp);

        TypeProps doubleTypeProp = new TypeProps(MysqlTypeConstants.DOUBLE, "15", false, false, "-1.0");
        javaDefaultMap.put("double", doubleTypeProp);
        javaDefaultMap.put("java.lang.DOUBLE", doubleTypeProp);


        TypeProps booleanProp = new TypeProps(MysqlTypeConstants.TINYINT, "4", false, false, "-1");
        javaDefaultMap.put("boolean", booleanProp);
        javaDefaultMap.put("java.lang.Boolean", booleanProp);


        javaDefaultMap.put("java.util.Date", new TypeProps(MysqlTypeConstants.DATETIME, "", false, false, "1000-01-01 00:00:00"));

        javaDefaultMap.put("java.lang.String", new TypeProps(MysqlTypeConstants.VARCHAR, "50", false, false, "\'\'"));

        TypeProps blogProp = new TypeProps(MysqlTypeConstants.BLOB, "", false, false, "\'\'");
        javaDefaultMap.put("java.lang.Byte", blogProp);
        javaDefaultMap.put("byte[]", blogProp);
        javaDefaultMap.put("java.math.BigDecimal", new TypeProps(MysqlTypeConstants.DECIMAL, "13,4", false, false, "-1"));

        TypeProps shortProp = new TypeProps(MysqlTypeConstants.MEDIUMINT, "6", false, false, "-1");
        javaDefaultMap.put("short", shortProp);
        javaDefaultMap.put("java.lang.Short", shortProp);

        String[] stringType = new String[]{MysqlTypeConstants.VARCHAR, MysqlTypeConstants.TEXT, MysqlTypeConstants.CHAR};
        javaRecommendMap.put("java.lang.String", stringType);

        String[] dateType = new String[]{MysqlTypeConstants.DATETIME, MysqlTypeConstants.DATE, MysqlTypeConstants.TIME, MysqlTypeConstants.TIMESTAMP};

        javaRecommendMap.put("java.util.Date", dateType);
    }


    public static TypeProps getType(String type) {
        return javaDefaultMap.get(type);
    }


    public static String[] getRecommendTypes(String fieldType) {
        return javaRecommendMap.get(fieldType);
    }
}
