package com.ccnode.codegenerator.dialog;

import com.ccnode.codegenerator.dialog.dto.mybatis.ClassMapperMethod;
import com.ccnode.codegenerator.dialog.dto.mybatis.ColumnAndField;
import com.ccnode.codegenerator.dialog.dto.mybatis.MapperMethodEnum;
import com.ccnode.codegenerator.enums.MethodName;
import com.ccnode.codegenerator.util.GenCodeUtil;
import com.ccnode.codegenerator.view.GenerateMethodXmlAction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bruce.ge on 2016/12/29.
 */
public class MapperUtil {

    public static final String SELECT = "select";
    public static final String FROM = "from";

    @Nullable
    static String generateSql(List<GenCodeProp> newAddedProps, List<ColumnAndField> deleteFields, String sqlText, List<ColumnAndField> existingFields) {
        String beforeWhere = sqlText;
        //text before where make it on it.
        int start = 0;
        int end = sqlText.length();
        String lowerSqlText = sqlText.toLowerCase();
        int where = findMatchFor(lowerSqlText, FROM);
        if (where != -1) {
            end = where;
            beforeWhere = sqlText.substring(0, where);
        }
        int select = findMatchFor(lowerSqlText, SELECT);
        if (select != -1) {
            start = select + SELECT.length();
            beforeWhere = beforeWhere.substring(select + SELECT.length());
        }

        //not support for with select function ect.
        if (beforeWhere.contains("(")) {
            return null;
        }
        String[] split = beforeWhere.split(",");

        List<String> beforeFormatted = new ArrayList<>();
        for (String uu : split) {
            String term = trimUseLess(uu);
            boolean isDeleted = false;
            for (ColumnAndField deleteField : deleteFields) {
                if (term.toLowerCase().equals(deleteField.getColumn().toLowerCase())) {
                    isDeleted = true;
                    break;
                }
            }
            if (isDeleted) {
                continue;
            }
            beforeFormatted.add(uu);
        }
        String beforeInsert = "";
        for (int i = 0; i < beforeFormatted.size(); i++) {
            beforeInsert += beforeFormatted.get(i);
            if (i != beforeFormatted.size() - 1) {
                beforeInsert += ",";
            }
        }

        String newAddInsert = "";

        for (int i = 0; i < newAddedProps.size(); i++) {
            newAddInsert += ",";
            newAddInsert += GenCodeUtil.wrapComma(newAddedProps.get(i).getColumnName());
            newAddInsert += "\n" + GenCodeUtil.TWO_RETRACT;
        }

        String newValueText = sqlText.substring(0, start) + beforeInsert + newAddInsert + sqlText.substring(end);
        return newValueText;
    }

    private static int findMatchFor(String lowerSqlText, String where) {
        Pattern matcher = Pattern.compile("\\b" + where + "\\b");
        Matcher matcher1 = matcher.matcher(lowerSqlText);
        if (matcher1.find()) {
            return matcher1.start();
        } else {
            return -1;
        }
    }

    private static String trimUseLess(String uu) {
        int len = uu.length();
        int start = 0;
        int end = uu.length();
        char c;
        c = uu.charAt(start++);
        while (start != len && (c == '\n') || (c == ' ') || c == '\t' || c == '`') {
            c = uu.charAt(start++);
        }
        if (start == len) {
            return "";
        }
        c = uu.charAt(--end);
        while (end >= start && (c == '\n') || (c == ' ') || c == '\t' || c == '`') {
            c = uu.charAt(--end);
        }
        return uu.substring(start - 1, end + 1);
    }

    public static String generateMapperMethod(List<GenCodeProp> newAddedProps, List<ColumnAndField> deletedFields, MapperMethodEnum type, ClassMapperMethod classMapperMethod) {
        String methodName = classMapperMethod.getMethodName();
        if(methodName.equals(MethodName.insert.name())){
            //todo generate the replace text for it.
            //for all the property to use.
            

        }

        return null;
    }

    public static String extractTable(String insertText) {
        if (insertText.length() == 0) {
            return null;
        }
        String formattedInsert = formatBlank(insertText).toLowerCase();
        int i = formattedInsert.indexOf(GenerateMethodXmlAction.INSERT_INTO);
        if (i == -1) {
            return null;
        }
        int s = i + GenerateMethodXmlAction.INSERT_INTO.length() + 1;
        StringBuilder resBuilder = new StringBuilder();
        for (int j = s; j < formattedInsert.length(); j++) {
            char c = formattedInsert.charAt(j);
            if (!isBlankChar(c)) {
                resBuilder.append(c);
            } else {
                break;
            }
        }
        if (resBuilder.length() > 0) {
            return resBuilder.toString();
        } else {
            return null;
        }
    }

    private static String formatBlank(String insertText) {
        StringBuilder result = new StringBuilder();
        char firstChar = insertText.charAt(0);
        result.append(firstChar);
        boolean before = isBlankChar(firstChar);
        for (int i = 1; i < insertText.length(); i++) {
            char curChar = insertText.charAt(i);
            boolean cur = isBlankChar(curChar);
            if (cur && before) {
                continue;
            } else {
                result.append(curChar);
                before = cur;
            }
        }
        return result.toString();
    }

    private static boolean isBlankChar(char c) {
        if (c == ' ' || c == '\t' || c == '\n' || c == '(' || c == '<' || c == ')' || c == '>') {
            return true;
        }
        return false;
    }

    public static String extractClassShortName(String fullName) {
        String[] split = fullName.split("\\.");
        return split[split.length - 1];
    }
}
