package com.ccnode.codegenerator.dialog;

import com.ccnode.codegenerator.dialog.datatype.ClassFieldInfo;
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

    private static String JAVA_OFF = ".java";

    private static String XML_OFF = ".xml";


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

        this.propFields = PsiClassUtil.buildPropFieldInfo(psiClass);

        this.fieldTypeMap = GenCodeDialogUtil.extractMap(propFields);

        if (propFields.size() == 0) {
            // TODO: 2016/12/25

        }
        //gonna construct all the values for the table.

        Object[][] propData = MyJTable.getDatas(propFields);
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

        propTable = new MyJTable(propData, fieldTypeMap);

        jScrollPane = new JScrollPane(propTable);


        //let generate the jtable use it to display.
        setTitle("create new mybatis files");
        init();
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
            Object value = propTable.getValueAt(i, MyJTable.FIELDCOLUMNINDEX);
            prop.setFieldName(MyJTable.formatString(value));

            Object column = propTable.getValueAt(i, MyJTable.COLUMN_NAMECOLUMNINDEX);
            prop.setColumnName(MyJTable.formatString(column));

            Object type = propTable.getValueAt(i, MyJTable.TYPECOLUMNINDEX);
            prop.setFiledType(MyJTable.formatString(type));

            Object length = propTable.getValueAt(i, MyJTable.LENGTHCOLUMNINDEX);
            prop.setSize(MyJTable.formatString(length));

            Object unique = propTable.getValueAt(i, MyJTable.UNIQUECOLUMNINDEX);
            prop.setUnique(MyJTable.formatBoolean(unique));

            Object primary = propTable.getValueAt(i, MyJTable.PRIMARYCOLUMNINDEX);
            prop.setPrimaryKey(MyJTable.formatBoolean(primary));

            Object canbenull = propTable.getValueAt(i, MyJTable.CANBENULLCOLUMNINDEX);
            prop.setCanBeNull(MyJTable.formatBoolean(canbenull));

            Object defaultValue = propTable.getValueAt(i, MyJTable.DEFAULT_VALUECOLUMNINDEX);
            prop.setDefaultValue(MyJTable.formatString(defaultValue));
            if (prop.getPrimaryKey()) {
                toSeeResult.setPrimaryProp(prop);
            }
            props.add(prop);
        }

        if (toSeeResult.getPrimaryProp() == null) {
            Messages.showErrorDialog(myProject, "please set a primary key", "validate fail");
            return;
        }

        Path moduleSrc = Paths.get(moduleSrcPath);
        Map<InsertFileType, InsertFileProp> propMap = new HashMap<>();
        if (sqlFileRaidio.isSelected()) {
            InsertFileProp prop = new InsertFileProp();
            String nameText = sqlNameText.getText().trim();
            prop.setName(nameText);
            prop.setFolderPath(sqlPathText.getText().trim());
            prop.setFullPath(prop.getFolderPath() + "/" + nameText);
            propMap.put(InsertFileType.SQL, prop);
        }

        if (daoFileRaidio.isSelected()) {
            InsertFileProp prop = new InsertFileProp();
            String nameText = daoNameText.getText().trim();
            String daoPath = daoPathText.getText().trim();
            prop.setFolderPath(daoPath);
            prop.setFullPath(prop.getFolderPath() + "/" + nameText);
            prop.setName(nameText.substring(0, nameText.length() - JAVA_OFF.length()));
            //shall combine two path
            Path relativeToSouce = moduleSrc.relativize(Paths.get(daoPath));
            String relate = relativeToSouce.toString();
            relate = relate.replace("\\", ".");
            relate = relate.replace("/", ".");
            prop.setPackageName(relate);
            //remove .java ect.
            prop.setQutifiedName(relate + "." + prop.getName());
            propMap.put(InsertFileType.DAO, prop);
        }

        if (serviceFileRaidio.isSelected()) {
            InsertFileProp prop = new InsertFileProp();
            String nameText = serviceNameText.getText().trim();
            prop.setFolderPath(servicePathText.getText().trim());

            Path relativeToSouce = moduleSrc.relativize(Paths.get(servicePathText.getText()));
            String relate = relativeToSouce.toString();
            relate = relate.replace("\\", ".");
            relate = relate.replace("/", ".");

            prop.setPackageName(relate);
            prop.setFullPath(prop.getFolderPath() + "/" + nameText);
            prop.setName(nameText.substring(0, nameText.length() - JAVA_OFF.length()));
            //remove .java ect.
            prop.setQutifiedName(relate + "." + prop.getName());

            propMap.put(InsertFileType.SERVICE, prop);

        }

        if (mapperFileRaidio.isSelected()) {
            InsertFileProp prop = new InsertFileProp();
            prop.setName(mapperNameText.getText().trim());
            prop.setFolderPath(mapperPathText.getText().trim());
            prop.setFullPath(prop.getFolderPath() + "/" + prop.getName());
            propMap.put(InsertFileType.MAPPER_XML, prop);
        }

        toSeeResult.setFileProps(propMap);
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
            Object valueAt = propTable.getValueAt(i, MyJTable.COLUMN_NAMECOLUMNINDEX);
            String message = "column name is empty on row " + i;
            Validate.notNull(valueAt, message);
            if (!(valueAt instanceof String)) {
                throw new RuntimeException(message);
            }
            Validate.notBlank((String) valueAt, message);
        }
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


}
