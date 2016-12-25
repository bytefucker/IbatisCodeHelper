package com.ccnode.codegenerator.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Created by bruce.ge on 2016/12/25.
 */
public class GenCodeDialog extends DialogWrapper {

    private PsiClass psiClass;

    private List<GenCodeProp> propList;

    private String sqlPath;

    private Project myProject;

    private GenCodeType type = GenCodeType.INSERT;

    public GenCodeDialog(Project project, PsiClass psiClass) {
        super(project, true);
        myProject = project;
        this.psiClass = psiClass;
        //init with propList.
        String psiFileFolderPath = psiClass.getContainingFile().getVirtualFile().getParent().getPath();
        sqlPath = psiFileFolderPath;
        setTitle("choose what you wan't to do");
        setOKButtonText("next");
        init();
    }


    @Override
    protected void doOKAction() {
        if (type == GenCodeType.INSERT) {
            GenCodeInsertDialog genCodeInsertDialog = new GenCodeInsertDialog(myProject, psiClass);
            boolean b = genCodeInsertDialog.showAndGet();
            if (!b) {
                return;
            } else {
                super.doOKAction();
            }
        } else if (type == GenCodeType.UPDATE) {
            System.out.println("gonna update");
            return;
        }
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridBagLayout());
        GridBagConstraints bag = new GridBagConstraints();
        bag.gridx = 0;
        bag.gridy = 0;
        ButtonGroup group = new ButtonGroup();
        JRadioButton generatenewButton = new JRadioButton("new mybatis file", true);

        generatenewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                type = GenCodeType.INSERT;
            }
        });

        group.add(generatenewButton);

        jPanel.add(generatenewButton, bag);

        bag.gridx = 1;

        JRadioButton updateButton = new JRadioButton("update existing mybatis file");
        group.add(updateButton);

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                type = GenCodeType.UPDATE;
            }
        });

        jPanel.add(updateButton, bag);

        return jPanel;
    }
}
