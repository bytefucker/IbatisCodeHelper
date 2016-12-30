package com.ccnode.codegenerator.view;

import com.ccnode.codegenerator.dialog.MapperUtil;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

import static com.intellij.patterns.XmlPatterns.xmlAttributeValue;

/**
 * Created by bruce.ge on 2016/12/29.
 */
public class MyBatisJavaToXmlPsiReferenceContributor extends PsiReferenceContributor {

    private static Set<String> tagNames = new HashSet<String>() {{
        add("update");
        add("insert");
        add("select");
        add("delete");
    }};

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(xmlAttributeValue(),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                        PsiElement parent1 = element.getParent();
                        if (!(parent1 instanceof XmlAttribute)) {
                            return PsiReference.EMPTY_ARRAY;
                        }
                        XmlAttribute parent = (XmlAttribute) parent1;
                        String name = parent.getName();
                        if (!name.equals("id")) {
                            return PsiReference.EMPTY_ARRAY;
                        }
                        //check the file is mapper file.
                        XmlTag selectTag = parent.getParent();
                        if (!tagNames.contains(selectTag.getName())) {
                            return PsiReference.EMPTY_ARRAY;
                        }
                        XmlTag rootParent = (XmlTag) selectTag.getParent();
                        XmlAttribute namespaceAttribute = rootParent.getAttribute("namespace");
                        if (namespaceAttribute == null) {
                            return PsiReference.EMPTY_ARRAY;
                        }
                        String namespace = namespaceAttribute.getValue();
                        if (StringUtils.isEmpty(namespace)) {
                            return PsiReference.EMPTY_ARRAY;
                        }
                        PsiShortNamesCache shortNamesCache = PsiShortNamesCache.getInstance(element.getProject());
                        PsiClass[] classesByName = shortNamesCache.getClassesByName(MapperUtil.extractClassShortName(namespace)
                                , GlobalSearchScope.moduleScope(ModuleUtilCore.findModuleForPsiElement(element)));
                        PsiClass findedClass = null;
                        for (PsiClass psiClass : classesByName) {
                            if (psiClass.getQualifiedName().equals(namespace)) {
                                findedClass = psiClass;
                                break;
                            }
                        }

                        if (findedClass == null) {
                            return PsiReference.EMPTY_ARRAY;
                        }

                        String value = parent.getValue();

                        PsiMethod findMethod = null;
                        PsiMethod[] methods = findedClass.getMethods();
                        for (PsiMethod method : methods) {
                            if (method.getName().equals(value)) {
                                findMethod = method;
                            }
                        }
                        if (findMethod != null) {
                            return new PsiReference[]{new PsiJavaMethodReference((XmlAttributeValue) element, findMethod.getNameIdentifier())};
                        } else {
                            return PsiReference.EMPTY_ARRAY;
                        }
                    }
                });
    }
}