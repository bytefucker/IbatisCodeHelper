package com.ccnode.codegenerator.dialog.datatype;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bruce.ge on 2016/12/25.
 */
public class MySqlTypeUtil {

    private static Map<String, String> javaDefaultMap = new HashMap<>();

    private static Map<String, String[]> javaRecommendMap = new HashMap<>();


    private static Map<String, TypeDefault> typeDefaultMap = new HashMap<>();


    static {
        javaDefaultMap.put("int", MysqlTypeConstants.INTEGER);
        javaDefaultMap.put("java.lang.Integer", MysqlTypeConstants.INTEGER);

        typeDefaultMap.put(MysqlTypeConstants.INTEGER, new TypeDefault("11", "-1"));

        TypeDefault longDefault = new TypeDefault("15", "-1");
        javaDefaultMap.put("long", MysqlTypeConstants.BIGINT);
        javaDefaultMap.put("java.lang.Long", MysqlTypeConstants.BIGINT);

        typeDefaultMap.put(MysqlTypeConstants.BIGINT, longDefault);

        TypeDefault floatTypeProp = new TypeDefault("15", "-1.0");

        javaDefaultMap.put("float", MysqlTypeConstants.FLOAT);
        javaDefaultMap.put("java.lang.FLoat", MysqlTypeConstants.FLOAT);
        typeDefaultMap.put(MysqlTypeConstants.FLOAT, floatTypeProp);


        TypeDefault doubleTypeProp = new TypeDefault("15", "-1.0");
        javaDefaultMap.put("double", MysqlTypeConstants.DOUBLE);
        javaDefaultMap.put("java.lang.DOUBLE", MysqlTypeConstants.DOUBLE);

        typeDefaultMap.put(MysqlTypeConstants.DOUBLE, doubleTypeProp);

        TypeDefault booleanProp = new TypeDefault("4", "-1");
        javaDefaultMap.put("boolean", MysqlTypeConstants.TINYINT);
        javaDefaultMap.put("java.lang.Boolean", MysqlTypeConstants.TINYINT);

        typeDefaultMap.put(MysqlTypeConstants.TINYINT, booleanProp);

        TypeDefault dateTimeDefault = new TypeDefault("", "'1000-01-01 00:00:00'");
        javaDefaultMap.put("java.util.Date", MysqlTypeConstants.DATETIME);

        typeDefaultMap.put(MysqlTypeConstants.DATETIME, dateTimeDefault);


        TypeDefault varcharDefault = new TypeDefault("50", "\'\'");
        javaDefaultMap.put("java.lang.String", MysqlTypeConstants.VARCHAR);

        typeDefaultMap.put(MysqlTypeConstants.VARCHAR, varcharDefault);

        TypeDefault blogDefault = new TypeDefault("", "\'\'");
        javaDefaultMap.put("java.lang.Byte", MysqlTypeConstants.BLOB);
        javaDefaultMap.put("byte[]", MysqlTypeConstants.BLOB);
        typeDefaultMap.put(MysqlTypeConstants.BLOB, blogDefault);


        TypeDefault decimalDefault = new TypeDefault("13,4", "-1");
        javaDefaultMap.put("java.math.BigDecimal", MysqlTypeConstants.DECIMAL);
        typeDefaultMap.put(MysqlTypeConstants.DECIMAL, decimalDefault);

        TypeDefault shortDefault = new TypeDefault("6", "-1");
        javaDefaultMap.put("short", MysqlTypeConstants.MEDIUMINT);
        javaDefaultMap.put("java.lang.Short", MysqlTypeConstants.MEDIUMINT);
        typeDefaultMap.put(MysqlTypeConstants.MEDIUMINT, shortDefault);

        TypeDefault timeStampDefault = new TypeDefault(null, "CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP");
        typeDefaultMap.put(MysqlTypeConstants.TIMESTAMP, timeStampDefault);


        String[] stringType = new String[]{MysqlTypeConstants.VARCHAR, MysqlTypeConstants.TEXT, MysqlTypeConstants.CHAR};
        javaRecommendMap.put("java.lang.String", stringType);

        String[] dateType = new String[]{MysqlTypeConstants.DATETIME, MysqlTypeConstants.DATE, MysqlTypeConstants.TIME, MysqlTypeConstants.TIMESTAMP};

        javaRecommendMap.put("java.util.Date", dateType);
    }

    /*get the mysql type for java type.*/
    public static TypeProps getType(String type) {
        String m = javaDefaultMap.get(type);
        TypeDefault typeDefault = typeDefaultMap.get(m);
        TypeProps props = new TypeProps(m, typeDefault.getSize(), typeDefault.getDefaultValue());
        return props;
    }

    public static TypeDefault getTypeDefault(String type) {
        return typeDefaultMap.get(type);
    }


    public static String[] getRecommendTypes(String fieldType) {
        return javaRecommendMap.get(fieldType);
    }

    public static boolean isDefaultType(String type) {
        return javaDefaultMap.get(type) != null;
    }
}
