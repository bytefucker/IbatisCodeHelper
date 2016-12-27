package com.ccnode.codegenerator.dialog;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Created by bruce.ge on 2016/12/27.
 */
class CheckButtonRender extends JCheckBox implements TableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setSelected((value != null && ((Boolean) value).booleanValue()));
        return this;
    }
}
