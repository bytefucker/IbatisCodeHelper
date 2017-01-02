package com.ccnode.codegenerator.view;

import com.ccnode.codegenerator.dialog.dto.mybatis.ColumnAndField;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlText;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Created by bruce.ge on 2017/1/3.
 */
public class MapperXmlCompletionContributor extends CompletionContributor {

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        if (parameters.getCompletionType() != CompletionType.BASIC) {
            return;
        }
        PsiElement positionElement = parameters.getOriginalPosition();
        if (positionElement == null) {
            return;
        }
        PsiElement parent = positionElement.getParent();
        if (parent == null || !(parent instanceof XmlText)) {
            return;
        }
        PsiElement position = parameters.getPosition();
        String positionText = position.getText();
        String realStart = positionText.substring(0, positionText.length() - CompletionUtil.DUMMY_IDENTIFIER_TRIMMED.length());
        int m = realStart.lastIndexOf("`");
        if (m != -1) {
            String lastText = realStart.substring(m + 1);
            PsiFile originalFile = parameters.getOriginalFile();
            if (!(originalFile instanceof XmlFile)) {
                return;
            }
            XmlFile xmlFile = (XmlFile) originalFile;
            if (!xmlFile.getRootTag().getName().equals("mapper")) {
                return;
            }
            //get all the rootMap for it.
            XmlTag[] subTags =
                    xmlFile.getRootTag().getSubTags();
            List<ColumnAndField> columnAndFields = new ArrayList<>();
            for (XmlTag tag : subTags) {
                if (tag.getName().equals("resultMap")) {
                    columnAndFields.addAll(generateColumnNames(tag));
                }
            }
            Set<String> columns = extractColumn(columnAndFields);
            int firstStart = findFindAlpha(realStart);
            columns.forEach((item) -> {
                boolean b = item.startsWith(lastText);
                if (b) {
                    result.addElement(LookupElementBuilder.create(realStart.substring(firstStart, m + 1) + item + "`"));
                }
            });
        }


    }

    private int findFindAlpha(String realStart) {

        for (int i = 0; i < realStart.length(); i++) {
            if (Character.isLetterOrDigit(realStart.charAt(i))) {
                return i;
            }
        }
        return 0;
    }

    private Set<String> extractField(List<ColumnAndField> columnAndFields) {
        Set<String> fields = new HashSet<>();
        for (ColumnAndField columnAndField : columnAndFields) {
            if (StringUtils.isNotBlank(columnAndField.getField())) {
                fields.add(columnAndField.getField());
            }
        }
        return fields;
    }

    private Set<String> extractColumn(List<ColumnAndField> columnAndFields) {
        Set<String> columns = new HashSet<>();
        for (ColumnAndField columnAndField : columnAndFields) {
            if (StringUtils.isNotBlank(columnAndField.getColumn())) {
                columns.add(columnAndField.getColumn());
            }
        }
        return columns;
    }

    private List<ColumnAndField> generateColumnNames(XmlTag tag) {
        List<ColumnAndField> column = new ArrayList<>();
        if (tag.getSubTags() != null) {
            for (XmlTag subTag : tag.getSubTags()) {
                ColumnAndField columnAndField = new ColumnAndField();
                String columnName = subTag.getAttributeValue("column");
                String field = subTag.getAttributeValue("property");
                columnAndField.setField(field);
                columnAndField.setColumn(columnName);
                column.add(columnAndField);
            }
        }
        return column;
    }
}
