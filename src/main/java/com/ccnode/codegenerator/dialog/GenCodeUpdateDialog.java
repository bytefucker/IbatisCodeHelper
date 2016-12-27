package com.ccnode.codegenerator.dialog;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiSearchHelper;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.Consumer;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by bruce.ge on 2016/12/27.
 */
public class GenCodeUpdateDialog extends DialogWrapper {
    private Project myProject;

    private PsiClass myClass;

    private JTextField jTextField;

    private XmlFile myXmlFile;

    private PsiClass nameSpaceDaoClass;

    public GenCodeUpdateDialog(Project project, PsiClass psiClass) {
        super(project, true);
        this.myProject = project;
        this.myClass = psiClass;
        jTextField = new JTextField();
        jTextField.setEditable(false);
        //go to find the corresponding xml if we can find.
        findXmlFileLocation();
        setTitle("generate update for your mapper xml");
        setOKButtonText("next");
        init();
    }

    private void findXmlFileLocation() {
        PsiSearchHelper searchService = ServiceManager.getService(myProject, PsiSearchHelper.class);
        java.util.List<XmlFile> xmlFiles = new ArrayList<XmlFile>();
        searchService.processUsagesInNonJavaFiles(myClass.getName(), (file, startOffset, endOffset) -> {
            if (file instanceof XmlFile) {
                XmlFile xmlFile = (XmlFile) file;
                if (xmlFile.getRootTag() != null && xmlFile.getRootTag().getName().equals("mapper")) {
                    if (xmlFile.getText().contains(myClass.getQualifiedName())) {
                        xmlFiles.add(xmlFile);
                        return false;
                    }
                }
            }
            return true;
        }, GlobalSearchScope.moduleScope(ModuleUtilCore.findModuleForPsiElement(myClass)));
        if (xmlFiles.size() > 0) {
            jTextField.setText(xmlFiles.get(0).getVirtualFile().getPath());
            myXmlFile = xmlFiles.get(0);
        }
    }


    @Override
    protected boolean postponeValidation() {
        return super.postponeValidation();
    }


    @Override
    protected void doOKAction() {
        boolean valid = validateInput();
        if (!valid) {
            return;
        }
        UpdateDialogMore updateDialogMore = new UpdateDialogMore(myProject, myClass, myXmlFile,nameSpaceDaoClass);
        boolean b = updateDialogMore.showAndGet();
        if (!b) {
            return;
        } else {
            super.doOKAction();
            return;
        }
    }

    private boolean validateInput() {
        if (myXmlFile == null) {
            Messages.showErrorDialog("the file is empty", "validate fail");
            return false;
        }
        if (!myXmlFile.getText().contains("mapper")) {
            Messages.showErrorDialog("the file choosed is not valid mapper for mybatis", "validate fail");
            return false;
        }

        if (!myXmlFile.getText().contains(myClass.getQualifiedName())) {
            Messages.showErrorDialog("the file do not contain " + myClass.getQualifiedName() + " as result Map", "validate fail");
            return false;
        }
        XmlTag rootTag =
                myXmlFile.getRootTag();
        if (rootTag == null) {
            Messages.showErrorDialog("the xml file root tag is empty", "validate fail");
            return false;
        }

        XmlAttribute namespaceAttribute =
                rootTag.getAttribute("namespace");
        if(namespaceAttribute==null){
            Messages.showErrorDialog("the xml file namespaceAttribute is empty", "validate fail");
            return false;
        }

        String namespace = namespaceAttribute.getValue();
        if (StringUtils.isBlank(namespace)) {
            Messages.showErrorDialog("the xml file namespace is empty", "validate fail");
            return false;
        }
        boolean findNameSpaceClass = false;
        String trimNamespace = namespace.trim();
        String[] split = trimNamespace.split("\\.");
        if (split.length > 0) {
            PsiClass[] classesByName = PsiShortNamesCache.getInstance(myProject).getClassesByName(split[split.length - 1], GlobalSearchScope.moduleScope(ModuleUtilCore.findModuleForPsiElement(myClass)));
            if (classesByName.length > 0) {
                for (PsiClass psiClass : classesByName) {
                    if (psiClass.getQualifiedName().equals(trimNamespace)) {
                        findNameSpaceClass = true;
                        this.nameSpaceDaoClass = psiClass;
                        break;
                    }
                }
            }
        }
        if (!findNameSpaceClass) {
            Messages.showErrorDialog("can't find the xml name space class", "validate fail");
            return false;
        }
        return true;

    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel jPanel = new JPanel();
        GridBagConstraints bag = new GridBagConstraints();
        jPanel.setLayout(new GridBagLayout());
        JLabel mapperLocationLable = new JLabel("choose your old mapper xml location:");

        bag.gridx = 0;
        bag.gridy = 0;
        jPanel.add(mapperLocationLable, bag);

        bag.gridx = 1;
        jPanel.add(jTextField, bag);

        bag.gridx = 2;
        JButton mapperButton = new JButton("open folder");
        mapperButton.addActionListener(e -> {
            getOKAction().setEnabled(true);
            FileChooserDescriptor fcd = FileChooserDescriptorFactory.createSingleFileDescriptor("xml");
            fcd.setShowFileSystemRoots(true);
            fcd.setTitle("Choose a file...");
            fcd.setHideIgnored(false);
//                fcd.setRoots(psiClass.getContainingFile().getVirtualFile().getParent());
            FileChooser.chooseFiles(fcd, myProject, myProject.getBaseDir(), new Consumer<java.util.List<VirtualFile>>() {
                @Override
                public void consume(java.util.List<VirtualFile> files) {
                    jTextField.setText(files.get(0).getPath());
                    PsiFile file = PsiManager.getInstance(myProject).findFile(files.get(0));
                    if (file != null && (file instanceof XmlFile)) {
                        myXmlFile = (XmlFile) file;
                    }
                }
            });
        });
        jPanel.add(mapperButton, bag);
        return jPanel;
    }
}
