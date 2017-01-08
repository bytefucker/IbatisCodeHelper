package com.ccnode.codegenerator.dialog;

import com.ccnode.codegenerator.dialog.datatype.ClassFieldInfo;
import com.ccnode.codegenerator.dialog.dto.MapperDto;
import com.ccnode.codegenerator.dialog.dto.mybatis.*;
import com.ccnode.codegenerator.enums.MethodName;
import com.ccnode.codegenerator.pojo.FieldToColumnRelation;
import com.ccnode.codegenerator.util.DateUtil;
import com.ccnode.codegenerator.util.PsiClassUtil;
import com.google.common.collect.Sets;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.Consumer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.List;

import static com.ccnode.codegenerator.dialog.MyJTable.formatBoolean;
import static com.ccnode.codegenerator.dialog.MyJTable.formatString;

/**
 * Created by bruce.ge on 2016/12/27.
 */
public class UpdateDialogMore extends DialogWrapper {


    private final Set<String> defaultMethodSet = Sets.newHashSet(MethodName.insert.name(), MethodName.insertList.name(), MethodName.update.name());
    public static final String PARAMANNOSTART = "@Param(\"";
    private Project myProject;

    private PsiClass myClass;

    private XmlFile myXmlFile;

    private List<ClassFieldInfo> newAddFields;

    private List<ColumnAndField> deletedFields;

    private PsiClass myDaoClass;

    private List<ClassFieldInfo> propFields;

    private List<ColumnAndField> existingFields;

    private String message;

    private MapperDto mapperDto;

    private Map<String, String> addedFieldTypeMap;

    private JTable myTable;

    private JLabel sqlLable = new JLabel("sql file name:");

    private JLabel sqlPathLable = new JLabel("sql file path:");

    private JRadioButton sqlFileRaidio = new JRadioButton("generate updated sql", true);

    private JButton sqlOpenFolder = new JButton("open folder");

    private JTextField sqlNameText;

    private JTextField sqlPathText;


    private List<JCheckWithResultMap> jCheckWithResultMaps;

    private List<JCheckWithMapperSql> jCheckWithMapperSqls;

    private List<JcheckWithMapperMethod> jcheckWithMapperMethods;

    @Override
    protected void doOKAction() {
        try {
            validateInput();
        } catch (Exception e) {
            Messages.showErrorDialog(myProject, e.getMessage(), "validate fail");
            return;
        }
        List<GenCodeProp> newAddedProps = new ArrayList<>();
        for (int i = 0; i < newAddFields.size(); i++) {
            GenCodeProp prop = new GenCodeProp();
            Object value = myTable.getValueAt(i, MyJTable.FIELDCOLUMNINDEX);
            prop.setFieldName(formatString(value));

            Object column = myTable.getValueAt(i, MyJTable.COLUMN_NAMECOLUMNINDEX);
            prop.setColumnName(formatString(column));

            Object type = myTable.getValueAt(i, MyJTable.TYPECOLUMNINDEX);
            prop.setFiledType(formatString(type));

            Object length = myTable.getValueAt(i, MyJTable.LENGTHCOLUMNINDEX);
            prop.setSize(formatString(length));

            Object unique = myTable.getValueAt(i, MyJTable.UNIQUECOLUMNINDEX);
            prop.setUnique(formatBoolean(unique));

            Object primary = myTable.getValueAt(i, MyJTable.PRIMARYCOLUMNINDEX);
            prop.setPrimaryKey(formatBoolean(primary));

            Object canbenull = myTable.getValueAt(i, MyJTable.CANBENULLCOLUMNINDEX);
            prop.setCanBeNull(formatBoolean(canbenull));

            Object defaultValue = myTable.getValueAt(i, MyJTable.DEFAULT_VALUECOLUMNINDEX);
            prop.setDefaultValue(formatString(defaultValue));
            newAddedProps.add(prop);
        }
        PsiDocumentManager manager = PsiDocumentManager.getInstance(myProject);
        XmlDocument xmlDocument = myXmlFile.getDocument();
        this.jCheckWithResultMaps.forEach((item) -> {
            if (item.getjCheckBox().isSelected()) {
                handleWithResultMap(newAddedProps, deletedFields, item.getResultMap());
            }
        });

        this.jCheckWithMapperSqls.forEach((item) -> {
            if (item.getjCheckBox().isSelected()) {
                handleWithSql(newAddedProps, deletedFields, item.getMapperSql());
            }
        });

        this.jcheckWithMapperMethods.forEach((item) -> {
            if (item.getjCheckBox().isSelected()) {
                handleWithMapperMethod(newAddedProps, deletedFields, item.getMapperMethod(), item.getClassMapperMethod());
            }
        });

        if (this.sqlFileRaidio.isSelected()) {
            //generate sql file base on add prop.
            String tableName = extractTableName();
            List<String> retList = new ArrayList<>();
            for (GenCodeProp field : newAddedProps) {
                StringBuilder ret = new StringBuilder();
                ret.append("ALTER TABLE " + tableName + " ADD " + field.getColumnName());
                ret.append(" " + field.getFiledType());
                if (org.apache.commons.lang.StringUtils.isNotBlank(field.getSize())) {
                    ret.append("(" + field.getSize() + ")");
                }
                if (field.getUnique()) {
                    ret.append(" UNIQUE");
                }
                if (!field.getCanBeNull()) {
                    ret.append(" NOT NULL");
                }

                if (org.apache.commons.lang.StringUtils.isNotBlank(field.getDefaultValue())) {
                    ret.append(" DEFAULT " + field.getDefaultValue());
                }
                if (field.getPrimaryKey()) {
                    ret.append(" AUTO_INCREMENT");
                }
                ret.append(" COMMENT '" + field.getFieldName() + "';");
                retList.add(ret.toString());
            }

            for (ColumnAndField deletedField : this.deletedFields) {
                StringBuilder ret = new StringBuilder();
                ret.append("ALTER TABLE " + tableName + " DROP COLUMN ");
                ret.append(deletedField.getColumn() + " ");
                retList.add(ret.toString());
            }

            String sqlFileName = sqlNameText.getText().trim();
            String sqlFileFolder = sqlPathText.getText().trim();
            try {
                String filePath = sqlFileFolder + "/" + sqlFileName;
                Files.write(Paths.get(filePath), retList, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException e) {
                throw new RuntimeException("can't write file " + sqlFileName + " to path " + sqlFileFolder + "/" + sqlFileName);
            }

        }
        super.doOKAction();
    }

    private String extractTableName() {
        String tableName = "";
        for (XmlTag tag : myXmlFile.getRootTag().getSubTags()) {
            if (tag.getName().equalsIgnoreCase("insert")) {
                String insertText = tag.getValue().getText();
                tableName = MapperUtil.extractTable(insertText);
                if (tableName != null) {
                    return tableName;
                }
            }
        }
        return tableName;
    }

    private void handleWithMapperMethod(List<GenCodeProp> newAddedProps, List<ColumnAndField> deletedFields, MapperMethod mapperMethod, ClassMapperMethod classMapperMethod) {
        String newValueText = MapperUtil.generateMapperMethod(newAddedProps, deletedFields, mapperMethod.getType(), classMapperMethod);
        if (newValueText == null) {
            return;
        } else {
            mapperMethod.getXmlTag().getValue().setText(newValueText);
        }
        //else set with value.

    }

    private void handleWithSql(List<GenCodeProp> newAddedProps, List<ColumnAndField> deletedFields, MapperSql mapperSql) {
        String sqlText = mapperSql.getTag().getValue().getText();
        String newValueText = MapperUtil.generateSql(newAddedProps, deletedFields, sqlText, this.existingFields);
        if (newValueText == null) return;
        WriteCommandAction.runWriteCommandAction(myProject, () -> {
            mapperSql.getTag().getValue().setText(newValueText);
        });
    }

    private void handleWithResultMap(List<GenCodeProp> newAddedProps, List<ColumnAndField> deletedFields, ResultMap resultMap) {
        for (XmlTag tag : resultMap.getTag().getSubTags()) {
            String property = tag.getAttributeValue("property");
            if (StringUtils.isNotBlank(property)) {
                for (ColumnAndField columnAndField : deletedFields) {
                    if (property.equals(columnAndField.getField().toLowerCase())) {
                        //go remove it.
                        WriteCommandAction.runWriteCommandAction(myProject, () -> {
                            tag.delete();
                        });
                    }
                }
            }
        }
        //maybe the new prop exist in the xmlTag

        for (GenCodeProp prop : newAddedProps) {
            XmlTag result = resultMap.getTag().createChildTag("result", "", "", false);
            result.setAttribute("column", prop.getColumnName());
            result.setAttribute("property", prop.getFieldName());
            WriteCommandAction.runWriteCommandAction(myProject, () -> {
                resultMap.getTag().addSubTag(result, false);
            });
        }
    }

    private void validateInput() {
        for (int i = 0; i < newAddFields.size(); i++) {
            Object valueAt = myTable.getValueAt(i, MyJTable.COLUMN_NAMECOLUMNINDEX);
            String message = "column name is empty on row " + i;
            Validate.notNull(valueAt, message);
            if (!(valueAt instanceof String)) {
                throw new RuntimeException(message);
            }
            Validate.notBlank((String) valueAt, message);
        }

        if (sqlFileRaidio.isSelected()) {
            Validate.notBlank(sqlNameText.getText(), "sql name is empty");
            Validate.notBlank(sqlPathText.getText(), "sql path is empty");
        }

    }

    public UpdateDialogMore(Project project, PsiClass srcClass, XmlFile xmlFile, PsiClass nameSpaceDaoClass) {
        super(project, true);
        this.myProject = project;
        this.myClass = srcClass;
        this.myXmlFile = xmlFile;
        this.myDaoClass = nameSpaceDaoClass;
        initNeedUpdate();
        sqlNameText = new JTextField(srcClass.getName() + "_update_" + DateUtil.formatYYYYMMDD(new Date()) + ".sql");
        sqlPathText = new JTextField(myClass.getContainingFile().getVirtualFile().getParent().getPath());
        setTitle("update mapper xml");
        init();
    }

    private void initNeedUpdate() {
        this.propFields = PsiClassUtil.buildPropFieldInfo(myClass);
        mapperDto = parseXml();
        //get the new added filed and type.
        extractAddAndDelete();
        if (newAddFields.size() == 0 && deletedFields.size() == 0) {
            message = "there is no field to update or add, please check again with your resultMap";
            return;
        }
        this.addedFieldTypeMap = GenCodeDialogUtil.extractMap(newAddFields);
        Object[][] datas = MyJTable.getDatas(newAddFields);
        this.myTable = new MyJTable(datas, this.addedFieldTypeMap);
        addWithCheckBoxs();
    }

    private void addWithCheckBoxs() {
        this.jCheckWithResultMaps = new ArrayList<>();
        mapperDto.getResultMapList().forEach((item) -> {
            JCheckWithResultMap e = new JCheckWithResultMap();
            e.setjCheckBox(new JCheckBox("resultMap id=" + item.getId(), true));
            e.setResultMap(item);
            jCheckWithResultMaps.add(e);
        });

        this.jCheckWithMapperSqls = new ArrayList<>();
        mapperDto.getSqls().forEach((item) -> {
            XmlTag tag = item.getTag();
            String text = tag.getValue().getText();
            boolean containField = false;

            for (ColumnAndField existingField : existingFields) {
                if (text.contains(existingField.getColumn())) {
                    containField = true;
                    break;
                }
            }
            if (containField) {
                JCheckWithMapperSql e = new JCheckWithMapperSql();
                e.setMapperSql(item);
                e.setjCheckBox(new JCheckBox("sql id=" + item.getId(), true));
                jCheckWithMapperSqls.add(e);
            }
        });

        jcheckWithMapperMethods = new ArrayList<>();

        List<ClassMapperMethod> methods = new ArrayList<>();
        PsiMethod[] allMethods = this.myDaoClass.getAllMethods();
        for (PsiMethod method : allMethods) {
            PsiParameter[] parameters = method.getParameterList().getParameters();
            for (PsiParameter psiParameter : parameters) {
                String typeText = psiParameter.getType().getCanonicalText();
                if (typeText.contains(myClass.getQualifiedName())) {
                    ClassMapperMethod mapperMethod = new ClassMapperMethod();
                    mapperMethod.setMethodName(method.getName());
                    if (typeText.startsWith("java.util")) {
                        mapperMethod.setList(true);
                    }
                    if (psiParameter.getText().startsWith(PARAMANNOSTART)) {
                        mapperMethod.setParamAnno(extractParam(psiParameter.getText()));
                    }
                    methods.add(mapperMethod);
                    break;
                }
            }
        }


        //todo make to replace the default method for user.
        methods.forEach((item) -> {
            MapperMethod mapperMethod = mapperDto.getMapperMethodMap().get(item.getMethodName());
            if (mapperMethod != null && defaultMethodSet.contains(item.getMethodName())) {
                JcheckWithMapperMethod e = new JcheckWithMapperMethod();
                e.setjCheckBox(new JCheckBox("replace " + mapperMethod.getType().name() + " mapper id " + item.getMethodName(), false));
                e.setClassMapperMethod(item);
                e.setMapperMethod(mapperMethod);
                jcheckWithMapperMethods.add(e);
            }
        });


    }

    private String extractParam(String typeText) {
        String paramAnno = "";
        int i = PARAMANNOSTART.length();
        while (typeText.charAt(i) != '\"' && i < typeText.length()) {
            paramAnno += typeText.charAt(i);
            i++;
        }
        return paramAnno;
    }

    private void extractAddAndDelete() {
        newAddFields = new ArrayList<>();
        deletedFields = new ArrayList<>();
        Set<String> allFieldMap = new HashSet<>();
        propFields.forEach((item) -> {
            allFieldMap.add(item.getFieldName().toLowerCase());
        });
        Set<String> existingMap = new HashSet<>();

        existingFields.forEach((item) -> existingMap.add(item.getField().toLowerCase()));

        propFields.forEach((item) -> {
            if (!existingMap.contains(item.getFieldName().toLowerCase())) {
                newAddFields.add(item);
            }
        });

        existingFields.forEach((item) -> {
            if (!allFieldMap.contains(item.getField().toLowerCase())) {
                deletedFields.add(item);
            }
        });
    }

    private MapperDto parseXml() {
        XmlTag[] subTags = myXmlFile.getRootTag().getSubTags();
        MapperDto dto = new MapperDto();
        List<ResultMap> resultMaps = new ArrayList<>();
        List<MapperSql> sqls = new ArrayList<>();
        Map<String, MapperMethod> methodMap = new HashMap<>();
        for (XmlTag subTag : subTags) {
            String name = subTag.getName();
            switch (name) {
                case "resultMap": {
                    ResultMap resultMap = buildResultMap(subTag);
                    if (resultMap.getType().equals(myClass.getQualifiedName())) {
                        existingFields = extractFileds(subTag);
                    }
                    resultMaps.add(resultMap);
                    break;
                }
                case "sql": {
                    MapperSql sql = buildSql(subTag);
                    sqls.add(sql);
                    break;
                }
                case "insert": {
                    MapperMethod s = extractMethod(subTag, MapperMethodEnum.INSERT);
                    String id = subTag.getAttributeValue("id");
                    methodMap.put(id, s);
                    break;
                }
                case "update": {
                    MapperMethod s = extractMethod(subTag, MapperMethodEnum.UPDATE);
                    String id = subTag.getAttributeValue("id");
                    methodMap.put(id, s);
                    break;
                }
                case "delete": {
                    MapperMethod s = extractMethod(subTag, MapperMethodEnum.DELETE);
                    String id = subTag.getAttributeValue("id");
                    methodMap.put(id, s);
                    break;
                }
                case "select": {
                    MapperMethod s = extractMethod(subTag, MapperMethodEnum.SELECT);
                    String id = subTag.getAttributeValue("id");
                    methodMap.put(id, s);
                    break;
                }
            }

        }

        if (existingFields == null) {
            //ask user to generate allColumnMap.
            GenerateResultMapDialog generateResultMapDialog = new GenerateResultMapDialog(myProject, PsiClassUtil.buildPropFields(myClass), myClass.getQualifiedName());
            boolean b = generateResultMapDialog.showAndGet();
            existingFields = new ArrayList<>();
            if (b) {
                FieldToColumnRelation relation = generateResultMapDialog.getRelation();
                Map<String, String> filedToColumnMap = relation.getFiledToColumnMap();
                for (String m : filedToColumnMap.keySet()) {
                    ColumnAndField e = new ColumnAndField();
                    e.setColumn(filedToColumnMap.get(m));
                    e.setField(m);
                    existingFields.add(e);
                }
            }
        }

        dto.setResultMapList(resultMaps);
        dto.setMapperMethodMap(methodMap);
        dto.setSqls(sqls);
        return dto;
    }

    private MapperMethod extractMethod(XmlTag subTag, MapperMethodEnum insert) {
        MapperMethod mapperMethod = new MapperMethod();
        mapperMethod.setType(insert);
        mapperMethod.setXmlTag(subTag);
        return mapperMethod;
    }

    private MapperSql buildSql(XmlTag subTag) {
        String id = subTag.getAttributeValue("id");
        MapperSql sql = new MapperSql();
        sql.setId(id);
        sql.setTag(subTag);
        return sql;
    }

    private List<ColumnAndField> extractFileds(XmlTag subTag) {
        List<ColumnAndField> props = new ArrayList<>();
        for (XmlTag tag : subTag.getSubTags()) {
            String property = tag.getAttributeValue("property");
            if (StringUtils.isNotBlank(property)) {
                ColumnAndField info = new ColumnAndField();
                info.setField(property);
                info.setColumn(tag.getAttributeValue("column"));
                props.add(info);
            }
        }
        return props;
    }

    private ResultMap buildResultMap(XmlTag subTag) {
        ResultMap resultMap = new ResultMap();
        String id = subTag.getAttributeValue("id");
        String type = subTag.getAttributeValue("type");
        resultMap.setId(id);
        resultMap.setType(type);
        resultMap.setTag(subTag);
        return resultMap;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridBagLayout());
        GridBagConstraints bag = new GridBagConstraints();
        if (message != null) {
            jPanel.add(new JLabel(message), bag);
            setOKActionEnabled(false);
            return jPanel;
        }

        //following are the added field.
        bag.anchor = GridBagConstraints.NORTHWEST;
        bag.fill = GridBagConstraints.HORIZONTAL;

        bag.gridy++;

        jPanel.add(new JLabel("the following are new added fields:"), bag);

        bag.gridy++;
        bag.gridx = 0;

        JScrollPane jScrollPane = new JScrollPane(myTable);

        bag.gridwidth = 10;

        jPanel.add(jScrollPane, bag);


        //following are deleted fields.
        bag.gridwidth = 1;
        if (deletedFields.size() > 0) {
            bag.gridy++;
            jPanel.add(new JLabel("the following are deleted fields:"), bag);
            bag.gridx++;
            for (ColumnAndField columnAndField : deletedFields) {
                jPanel.add(new JLabel(columnAndField.getField()), bag);
            }
        }

        bag.gridx = 0;

        bag.gridy++;

        bag.insets = new Insets(10, 0, 5, 10);
        jPanel.add(new JLabel("choose the statement you want to update:"), bag);


        bag.insets = new Insets(3, 3, 3, 3);
        bag.gridy++;
        for (JCheckWithResultMap resultMap : this.jCheckWithResultMaps) {
            bag.gridy++;
            jPanel.add(resultMap.getjCheckBox(), bag);
        }

        bag.gridy++;

        for (JCheckWithMapperSql sql : this.jCheckWithMapperSqls) {
            bag.gridy++;
            jPanel.add(sql.getjCheckBox(), bag);
        }

        bag.gridy++;


        for (JcheckWithMapperMethod method : this.jcheckWithMapperMethods) {
            bag.gridy++;
            jPanel.add(method.getjCheckBox(), bag);
        }

        bag.gridy++;

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
        return jPanel;
    }
}
