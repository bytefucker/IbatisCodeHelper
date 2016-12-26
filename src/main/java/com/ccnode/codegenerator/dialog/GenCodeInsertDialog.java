package com.ccnode.codegenerator.dialog;

import com.ccnode.codegenerator.dialog.datatype.*;
import com.ccnode.codegenerator.dialog.exception.NotStringException;
import com.ccnode.codegenerator.util.GenCodeUtil;
import com.ccnode.codegenerator.util.PsiClassUtil;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.util.Consumer;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bruce.ge on 2016/12/25.
 */
public class GenCodeInsertDialog extends DialogWrapper {

    public static final String COLUMNUNIQUE = "unique";
    public static final String FILEDCOLUMN = "filed";
    public static final String COLUMN_NAMECOLUMN = "columnName";

    public static final String TYPECOLUMN = "type";

    public static final String PRIMARYCOLUMN = "primary";

    public static final String LENGTHCOLUMN = "length";

    public static final String CANBENULLCOLUMN = "canBeNull";

    public static final String DEFAULT_VALUE_COLUMN = "defaultValue";


    private PsiClass psiClass;

    private List<GenCodeProp> propList;


    private String moduleSrcPath;

    private Map<String, String> fieldTypeMap;


    private List<ClassFieldInfo> propFields;


    private InsertDialogResult insertDialogResult;

    //we will generate value base on it.

    private JLabel sqlLable = new JLabel("sql file name:");

    private JLabel sqlPathLable = new JLabel("sql file path:");

    private JRadioButton sqlFileRaidio = new JRadioButton("generate sql", true);

    private JButton sqlOpenFolder = new JButton("open folder");

    private JTextField sqlNameText;

    private JTextField sqlPathText;


    private JLabel daoLable = new JLabel("dao file name:");

    private JLabel daoPathLable = new JLabel("dao file path:");

    private JRadioButton daoFileRaidio = new JRadioButton("generate dao", true);

    private JButton daoOpenFolder = new JButton("open folder");

    private JTextField daoNameText;

    private static String[] columnNames = {FILEDCOLUMN, COLUMN_NAMECOLUMN, TYPECOLUMN, LENGTHCOLUMN, COLUMNUNIQUE, PRIMARYCOLUMN, CANBENULLCOLUMN, DEFAULT_VALUE_COLUMN};

    private static final int FIELDCOLUMNINDEX = 0;

    private static final int COLUMN_NAMECOLUMNINDEX = 1;

    private static final int TYPECOLUMNINDEX = 2;
    private static final int LENGTHCOLUMNINDEX = 3;
    private static final int UNIQUECOLUMNINDEX = 4;
    private static final int PRIMARYCOLUMNINDEX = 5;
    private static final int CANBENULLCOLUMNINDEX = 6;

    private static final int DEFAULT_VALUECOLUMNINDEX = 7;

    private JTextField daoPathText;


    private JLabel mapperLable = new JLabel("mapper xml file name:");

    private JLabel mapperPathLable = new JLabel("mapper xml file path:");

    private JRadioButton mapperFileRaidio = new JRadioButton("generate mapper", true);

    private JButton mapperOpenFolder = new JButton("open folder");

    private JTextField mapperNameText;

    private JTextField mapperPathText;


    private JLabel serviceLable = new JLabel("service file name:");

    private JLabel servicePathLable = new JLabel("service file path:");

    private JRadioButton serviceFileRaidio = new JRadioButton("generate service", true);

    private JButton serviceOpenFolder = new JButton("open folder");

    private JTextField serviceNameText;

    private JTextField servicePathText;

    private Project myProject;

    private JLabel tableName = new JLabel("table name:");

    private JTextField tableNameText;

    private JScrollPane jScrollPane;

    private JTable propTable;

    public InsertDialogResult getInsertDialogResult() {
        return insertDialogResult;
    }

    public GenCodeInsertDialog(Project project, PsiClass psiClass) {
        super(project, true);
        myProject = project;
        this.psiClass = psiClass;

        this.propFields = PsiClassUtil.buildPropMap(psiClass);

        this.fieldTypeMap = extractMap(propFields);

        if (propFields.size() == 0) {
            // TODO: 2016/12/25

        }
        //gonna construct all the values for the table.

        Object[][] propData = getDatas(propFields, columnNames.length);
        //init with propList.
        String psiFileFolderPath = psiClass.getContainingFile().getVirtualFile().getParent().getPath();
        String className = psiClass.getName();

        tableNameText = new JTextField(GenCodeUtil.getUnderScore(className), 15);
        //the default folder name.
        sqlPathText = new JTextField(psiFileFolderPath);
        daoPathText = new JTextField(psiFileFolderPath);
        mapperPathText = new JTextField(psiFileFolderPath);
        servicePathText = new JTextField(psiFileFolderPath);

        sqlNameText = new JTextField(className + ".sql");

        mapperNameText = new JTextField(className + "Dao.xml");

        serviceNameText = new JTextField(className + "Service.java");

        daoNameText = new JTextField(className + "Dao.java");

        propTable = new JTable(propData, columnNames) {
            @Override
            public Dimension getPreferredScrollableViewportSize() {
                int headerHeight = propTable.getTableHeader().getPreferredSize().height;
                int height = headerHeight + (10 * getRowHeight());
                int width = getPreferredSize().width;
                return new Dimension(width, height);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }

            @Override
            public void setValueAt(Object aValue, int row, int column) {
                // TODO: 2016/12/26 could do better not using getColumn.
                super.setValueAt(aValue, row, column);
                if (column == TYPECOLUMNINDEX) {
                    TypeDefault typeDefault = MySqlTypeUtil.getTypeDefault((String) aValue);
                    if (typeDefault == null) {
                        super.setValueAt(null, row, LENGTHCOLUMNINDEX);
                        super.setValueAt(null, row, DEFAULT_VALUECOLUMNINDEX);
                    } else {
                        super.setValueAt(typeDefault.getSize(), row, LENGTHCOLUMNINDEX);
                        super.setValueAt(typeDefault.getDefaultValue(), row, DEFAULT_VALUECOLUMNINDEX);
                    }
                }
            }
        };

        propTable.getTableHeader().setReorderingAllowed(false);

        propTable.getColumn(COLUMNUNIQUE).setCellRenderer(new CheckButtonRender());
        propTable.getColumn(COLUMNUNIQUE).setCellEditor(new CheckButtonEditor(new JCheckBox()));


        propTable.getColumn(PRIMARYCOLUMN).setCellRenderer(new CheckButtonRender());
        propTable.getColumn(PRIMARYCOLUMN).setCellEditor(new CheckButtonEditor(new JCheckBox()));

        propTable.getColumn(CANBENULLCOLUMN).setCellRenderer(new CheckButtonRender());
        propTable.getColumn(CANBENULLCOLUMN).setCellEditor(new CheckButtonEditor(new JCheckBox()));


        propTable.getColumn(TYPECOLUMN).setCellRenderer(new MyComboBoxRender());

        propTable.getColumn(TYPECOLUMN).setCellEditor(new MyComboBoxEditor(new JComboBox()));
        propTable.setRowHeight(25);

        jScrollPane = new JScrollPane(propTable);


        propTable.setFillsViewportHeight(true);
        propTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        //let generate the jtable use it to display.
        setTitle("create new mybatis files");
        init();
    }

    private Map<String, String> extractMap(List<ClassFieldInfo> propFields) {
        Map<String, String> fieldTypeMap = new HashMap<>();
        for (ClassFieldInfo info : propFields) {
            fieldTypeMap.put(info.getFieldName(), info.getFieldType());
        }
        return fieldTypeMap;
    }

    private Object[][] getDatas(List<ClassFieldInfo> propFields, int columnLength) {
        Object[][] ss = new Object[propFields.size()][];
        for (int i = 0; i < propFields.size(); i++) {
            Object[] mm = new Object[columnLength];
            ClassFieldInfo info = propFields.get(i);

            mm[FIELDCOLUMNINDEX] = info.getFieldName();
            mm[COLUMN_NAMECOLUMNINDEX] = GenCodeUtil.getUnderScore(info.getFieldName());
            TypeProps typeProp = MySqlTypeUtil.getType(info.getFieldType());
            if (typeProp == null) {
                // TODO: 2016/12/25 ask user if ignore.
            }
            customTypeProp(info, typeProp);
            mm[TYPECOLUMNINDEX] = typeProp.getDefaultType();
            mm[LENGTHCOLUMNINDEX] = typeProp.getSize();
            mm[UNIQUECOLUMNINDEX] = typeProp.getUnique();
            mm[PRIMARYCOLUMNINDEX] = typeProp.getPrimary();
            mm[CANBENULLCOLUMNINDEX] = typeProp.getCanBeNull();
            mm[DEFAULT_VALUECOLUMNINDEX] = typeProp.getDefaultValue();
            ss[i] = mm;
        }
        return ss;
    }

    private void customTypeProp(ClassFieldInfo info, TypeProps typeProp) {
        if (info.getFieldName().equals("id")) {
            typeProp.setPrimary(true);
        } else if (info.getFieldName().toLowerCase().equals("updatetime")) {
            TypeDefault typeDefault = MySqlTypeUtil.getTypeDefault(MysqlTypeConstants.TIMESTAMP);
            typeProp.setDefaultType(MysqlTypeConstants.TIMESTAMP);
            typeProp.setDefaultValue(typeDefault.getDefaultValue());
            typeProp.setSize(typeDefault.getSize());
        }
    }


    @Override
    protected void doOKAction() {
        //first to check with all data.
        String qualifiedName = psiClass.getQualifiedName();
        String[] split = qualifiedName.split("\\.");
        int splitLength = split.length;

        VirtualFile psiFile = psiClass.getContainingFile().getVirtualFile();
        while (splitLength > 0) {
            psiFile = psiFile.getParent();
            splitLength--;
        }
        this.moduleSrcPath = psiFile.getPath();

        try {
            validateInput();
        } catch (Exception e) {
            Messages.showErrorDialog(myProject, e.getMessage(), "validate fail");
            return;
        }


        InsertDialogResult toSeeResult = new InsertDialogResult();
        //just go to set the value.
        List<GenCodeProp> props = new ArrayList<>();
        for (int i = 0; i < propFields.size(); i++) {
            GenCodeProp prop = new GenCodeProp();
            Object value = propTable.getValueAt(i, FIELDCOLUMNINDEX);
            prop.setFieldName(formatString(value));

            Object column = propTable.getValueAt(i, COLUMN_NAMECOLUMNINDEX);
            prop.setColumnName(formatString(column));

            Object type = propTable.getValueAt(i, TYPECOLUMNINDEX);
            prop.setFiledType(formatString(type));

            Object length = propTable.getValueAt(i, LENGTHCOLUMNINDEX);
            prop.setSize(formatString(length));

            Object unique = propTable.getValueAt(i, UNIQUECOLUMNINDEX);
            prop.setUnique(formatBoolean(unique));

            Object primary = propTable.getValueAt(i, PRIMARYCOLUMNINDEX);
            prop.setPrimaryKey(formatBoolean(primary));
            if (prop.getPrimaryKey()) {
                toSeeResult.setPrimaryKey(prop.getColumnName());
            }
            Object canbenull = propTable.getValueAt(i, CANBENULLCOLUMNINDEX);
            prop.setCanBeNull(formatBoolean(canbenull));

            Object defaultValue = propTable.getValueAt(i, DEFAULT_VALUECOLUMNINDEX);
            prop.setDefaultValue(formatString(defaultValue));
            props.add(prop);
        }

        Path moduleSrc = Paths.get(moduleSrcPath);
        List<InsertFileProp> insertFileProps = new ArrayList<>();
        if (sqlFileRaidio.isSelected()) {
            InsertFileProp prop = new InsertFileProp();
            prop.setType(InsertFileType.SQL);
            prop.setName(sqlNameText.getText());
            prop.setPath(sqlPathText.getText());
            insertFileProps.add(prop);
        }

        if (daoFileRaidio.isSelected()) {
            InsertFileProp prop = new InsertFileProp();
            prop.setType(InsertFileType.DAO);
            prop.setName(daoNameText.getText());
            prop.setPath(daoPathText.getText());
            insertFileProps.add(prop);

            //shall combine two path
            Path relativeToSouce = moduleSrc.relativize(Paths.get(daoPathText.getText()));
            String relate = relativeToSouce.toString();
            relate = relate.replace("\\", ".");
            relate = relate.replace("/", ".");
            toSeeResult.setDaoPackageName(relate);
        }

        if (serviceFileRaidio.isSelected()) {
            InsertFileProp prop = new InsertFileProp();
            prop.setType(InsertFileType.SERVICE);
            prop.setName(serviceNameText.getText());
            prop.setPath(servicePathText.getText());
            insertFileProps.add(prop);

            Path relativeToSouce = moduleSrc.relativize(Paths.get(servicePathText.getText()));
            String relate = relativeToSouce.toString();
            relate = relate.replace("\\", ".");
            relate = relate.replace("/", ".");
            toSeeResult.setServicePackageName(relate);
        }

        if (mapperFileRaidio.isSelected()) {
            InsertFileProp prop = new InsertFileProp();
            prop.setType(InsertFileType.MAPPER_XML);
            prop.setName(mapperNameText.getText());
            prop.setPath(mapperPathText.getText());
            insertFileProps.add(prop);
        }

        toSeeResult.setFileProps(insertFileProps);
        toSeeResult.setPropList(props);
        toSeeResult.setTableName(tableNameText.getText());
        this.insertDialogResult = toSeeResult;
        super.doOKAction();
    }

    private void validateInput() {
        //
        Path moduleSrc = Paths.get(this.moduleSrcPath);
        Validate.notBlank(tableNameText.getText(), "table name is empty");
        if (sqlFileRaidio.isSelected()) {
            Validate.notBlank(sqlNameText.getText(), "sql name is empty");
            Validate.notBlank(sqlPathText.getText(), "sql path is empty");
        }

        if (mapperFileRaidio.isSelected()) {
            Validate.isTrue(daoFileRaidio.isSelected(), "you shall select with dao file to generate");
            Validate.notBlank(mapperNameText.getText(), "mapper name is empty");
            Validate.notBlank(mapperPathText.getText(), "mapper path is empty");
        }

        if (daoFileRaidio.isSelected()) {
            Validate.notBlank(daoNameText.getText(), "dao name is empty");
            Validate.notBlank(daoPathText.getText(), "dao path is empty");
            Validate.isTrue(Paths.get(daoPathText.getText()).startsWith(moduleSrc), "your dao path shall be under " + this.moduleSrcPath);
        }

        if (serviceFileRaidio.isSelected()) {
            Validate.notBlank(serviceNameText.getText(), "service name is empty");
            Validate.notBlank(servicePathText.getText(), "service path is empty");
            Validate.isTrue(Paths.get(servicePathText.getText()).startsWith(moduleSrc), "your service path shall be under " + this.moduleSrcPath);
        }


        for (int i = 0; i < propFields.size(); i++) {
            Object valueAt = propTable.getValueAt(i, COLUMN_NAMECOLUMNINDEX);
            String message = "column name is empty on row " + i;
            Validate.notNull(valueAt, message);
            if (!(valueAt instanceof String)) {
                throw new RuntimeException(message);
            }
            Validate.notBlank((String) valueAt, message);
        }
    }

    private Boolean formatBoolean(Object unique) {
        if (unique == null) {
            return false;
        }

        if (!(unique instanceof Boolean)) {
            return false;
        }
        return (Boolean) unique;
    }

    private String formatString(Object value) {
        if (value == null) {
            return "";
        }
        if (!(value instanceof String)) {
            throw new NotStringException();
        }
        return ((String) value).trim();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridBagLayout());
        GridBagConstraints bag = new GridBagConstraints();
        int mygridy = 0;
        bag.gridy = mygridy++;
        bag.gridx = 0;
        jPanel.add(tableName, bag);

        bag.gridx = 1;
        jPanel.add(tableNameText, bag);

        bag.anchor = GridBagConstraints.NORTHWEST;
        bag.fill = GridBagConstraints.HORIZONTAL;
        bag.gridy++;
        bag.gridx = 0;
        bag.gridwidth = 10;

//        jScrollPane.setMinimumSize(jScrollPane.getPreferredSize());
        jPanel.add(jScrollPane, bag);

        mygridy += 1;
        bag.gridwidth = 1;
        bag.gridy = mygridy++;
        bag.gridx = 0;
        createPanel(jPanel, bag, sqlFileRaidio, sqlLable, sqlNameText, sqlPathLable, sqlPathText, sqlOpenFolder, myProject);

        bag.gridy = mygridy++;
        bag.gridx = 0;
        createPanel(jPanel, bag, daoFileRaidio, daoLable, daoNameText, daoPathLable, daoPathText, daoOpenFolder, myProject);

        bag.gridy = mygridy++;
        bag.gridx = 0;
        createPanel(jPanel, bag, mapperFileRaidio, mapperLable, mapperNameText, mapperPathLable, mapperPathText, mapperOpenFolder, myProject);

        bag.gridy = mygridy++;
        bag.gridx = 0;
        createPanel(jPanel, bag, serviceFileRaidio, serviceLable, serviceNameText, servicePathLable, servicePathText, serviceOpenFolder, myProject);
        return jPanel;
    }

    private static void createPanel(JPanel jPanel, GridBagConstraints bag, JRadioButton sqlFileRaidio, JLabel sqlLable, JTextField sqlNameText, JLabel sqlPathLable, final JTextField sqlPathText, JButton sqlOpenFolder, final Project myProject) {
        bag.gridx = 0;

        jPanel.add(sqlFileRaidio, bag);

        bag.gridx = 1;


        jPanel.add(sqlLable, bag);

        bag.gridx = 2;


        jPanel.add(sqlNameText, bag);

        bag.gridx = 3;

        jPanel.add(sqlPathLable, bag);

        bag.gridx = 4;
        jPanel.add(sqlPathText, bag);

        sqlOpenFolder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileChooserDescriptor fcd = FileChooserDescriptorFactory.createSingleFolderDescriptor();
                fcd.setShowFileSystemRoots(true);
                fcd.setTitle("Choose a folder...");
                fcd.setDescription("choose the path to store file");
                fcd.setHideIgnored(false);
//                fcd.setRoots(psiClass.getContainingFile().getVirtualFile().getParent());
                FileChooser.chooseFiles(fcd, myProject, myProject.getBaseDir(), new Consumer<List<VirtualFile>>() {
                    @Override
                    public void consume(List<VirtualFile> files) {
                        sqlPathText.setText(files.get(0).getPath());
                    }
                });
            }
        });
        bag.gridx = 5;
        jPanel.add(sqlOpenFolder, bag);
    }

    class CheckButtonRender extends JCheckBox implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setSelected((value != null && ((Boolean) value).booleanValue()));
            return this;
        }
    }


    class CheckButtonEditor extends DefaultCellEditor {
        public CheckButtonEditor(JCheckBox checkBox) {
            super(checkBox);
        }


    }

    class MyComboBoxRender implements TableCellRenderer {

        private Map<Integer, JComboBox> jComboBoxMap = new HashMap<>();

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (jComboBoxMap.get(row) == null) {
                JComboBox jComboBox = new JComboBox();
                Object fieldName = table.getValueAt(row, 0);
                String fieldType = fieldTypeMap.get(fieldName);
                String[] recommendTypes = MySqlTypeUtil.getRecommendTypes(fieldType);
                TypeProps type = MySqlTypeUtil.getType(fieldType);
                if (recommendTypes == null) {
                    jComboBox.addItem(type.getDefaultType());
                } else {
                    for (String recommend : recommendTypes) {
                        jComboBox.addItem(recommend);
                    }
                }
                jComboBoxMap.put(row, jComboBox);
            }
            JComboBox jComboBox = jComboBoxMap.get(row);
            if (value != null) {
                jComboBox.setSelectedItem(value);
                //
            }
            return jComboBox;
            //find the filedType.

        }
    }

    class MyComboBoxEditor extends DefaultCellEditor {

        private Map<Integer, String[]> itemMap = new HashMap<>();

        public MyComboBoxEditor(JComboBox comboBox) {
            super(comboBox);
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
}
