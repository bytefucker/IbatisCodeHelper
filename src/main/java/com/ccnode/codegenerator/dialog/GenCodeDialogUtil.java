package com.ccnode.codegenerator.dialog;

import com.ccnode.codegenerator.dialog.datatype.ClassFieldInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bruce.ge on 2016/12/28.
 */
public class GenCodeDialogUtil {
    static Map<String, String> extractMap(List<ClassFieldInfo> propFields) {
        Map<String, String> fieldTypeMap = new HashMap<>();
        for (ClassFieldInfo info : propFields) {
            fieldTypeMap.put(info.getFieldName(), info.getFieldType());
        }
        return fieldTypeMap;
    }
}
