package com.ccnode.codegenerator.dialog;

import com.ccnode.codegenerator.dialog.datatype.MySqlTypeUtil;
import com.ccnode.codegenerator.dialog.datatype.TypeProps;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bruce.ge on 2016/12/27.
 */
class MyComboBoxEditor extends DefaultCellEditor {

    private Map<Integer, String[]> itemMap = new HashMap<>();
    private Map<String, String> fieldTypeMap;

    public MyComboBoxEditor(JComboBox comboBox, Map<String, String> fieldTypeMap) {
        super(comboBox);
        this.fieldTypeMap = fieldTypeMap;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        JComboBox editorComponent
                = (JComboBox) this.editorComponent;
        editorComponent.removeAllItems();
        if (itemMap.get(row) == null) {
            Object fieldName = table.getValueAt(row, 0);
            String fieldType = fieldTypeMap.get(fieldName);
            String[] recommendTypes = MySqlTypeUtil.getRecommendTypes(fieldType);
            TypeProps type = MySqlTypeUtil.getType(fieldType);
            if (recommendTypes == null) {
                editorComponent.addItem(type.getDefaultType());
                itemMap.put(row, new String[]{type.getDefaultType()});
            } else {
                for (String recommend : recommendTypes) {
                    editorComponent.addItem(recommend);
                }
                itemMap.put(row, recommendTypes);
            }
        } else {
            for (String recommend : itemMap.get(row)) {
                editorComponent.addItem(recommend);
            }
        }

        return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }


}
