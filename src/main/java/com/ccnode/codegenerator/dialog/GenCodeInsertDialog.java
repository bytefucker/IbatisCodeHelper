package com.ccnode.codegenerator.dialog;

import com.ccnode.codegenerator.util.GenCodeUtil;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Created by bruce.ge on 2016/12/25.
 */
public class GenCodeInsertDialog extends DialogWrapper {

    private PsiClass psiClass;

    private List<GenCodeProp> propList;

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

    public GenCodeInsertDialog(Project project, PsiClass psiClass) {
        super(project, true);
        myProject = project;
        this.psiClass = psiClass;
        //init with propList.
        String psiFileFolderPath = psiClass.getContainingFile().getVirtualFile().getParent().getPath();
        String className = psiClass.getName();

        tableNameText = new JTextField(GenCodeUtil.getUnderScore(className));
        //the default folder name.
        sqlPathText = new JTextField(psiFileFolderPath);
        daoPathText = new JTextField(psiFileFolderPath);
        mapperPathText = new JTextField(psiFileFolderPath);
        servicePathText = new JTextField(psiFileFolderPath);

        sqlNameText = new JTextField(className + ".sql");

        mapperNameText = new JTextField(className + "Dao.xml");

        serviceNameText = new JTextField(className + "Service.java");

        daoNameText = new JTextField(className + "Dao.java");

        String[] columnNames = {"filed", "columnName", "type", "length", "unique", "primary", "canBeNull", "defaultValue"};

        Object[][] data = {
                {
                        "id", "id", "int", "11", true, true, false, 123
                },
                {
                        "username", "username", "varchar", "50", true, true, false, ""

                }
        };

        propTable = new JTable(data, columnNames) {
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
        };

        propTable.getTableHeader().setReorderingAllowed(false);

        propTable.getColumn("unique").setCellRenderer(new CheckButtonRender());
        propTable.getColumn("unique").setCellEditor(new CheckButtonEditor(new JCheckBox()));
        jScrollPane = new JScrollPane(propTable);


        propTable.setFillsViewportHeight(true);
        propTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        //let generate the jtable use it to display.
        setTitle("create new mybatis files");
        init();
    }

    @Override
    protected void setOKActionEnabled(boolean isEnabled) {
        //fill with values.

        //
        return;
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
}
