package com.ccnode.codegenerator.service.pojo;

import com.ccnode.codegenerator.dialog.GenCodeProp;
import com.ccnode.codegenerator.dialog.InsertDialogResult;
import com.ccnode.codegenerator.dialog.InsertFileProp;
import com.ccnode.codegenerator.util.DateUtil;
import com.ccnode.codegenerator.util.GenCodeUtil;
import com.google.common.collect.Lists;
import com.intellij.psi.PsiClass;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by bruce.ge on 2016/12/26.
 */
public class GenerateInsertCodeService {

    public static void generateInsert(InsertDialogResult insertDialogResult) {
        List<InsertFileProp> fileProps = insertDialogResult.getFileProps();
        ExecutorService executorService = Executors.newFixedThreadPool(fileProps.size());
        CountDownLatch latch = new CountDownLatch(fileProps.size());
        for (InsertFileProp fileProp : fileProps) {
            executorService.submit(() -> {
                try {
                    generateFiles(fileProp, insertDialogResult);
                } finally {
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
    }


    private static void generateFiles(InsertFileProp fileProp, InsertDialogResult insertDialogResult) {
        switch (fileProp.getType()) {
            case SQL: {
                generateSqlFile(fileProp, insertDialogResult.getPropList(), insertDialogResult.getPrimaryKey(), insertDialogResult.getTableName());
                break;
            }
            case DAO: {
                generateDaoFile(fileProp, insertDialogResult.getDaoPackageName(), insertDialogResult.getSrcClass());
                break;
            }
            case MAPPER_XML: {
                generateMapperXml(fileProp, insertDialogResult.getSrcClass(), insertDialogResult.getDaoPackageName(), insertDialogResult.getTableName(), insertDialogResult.getPrimaryKey());
                break;
            }
            case SERVICE: {
                generateService(fileProp, insertDialogResult.getDaoPackageName(), insertDialogResult.getSrcClass(), insertDialogResult.getServicePackageName());
                break;
            }
        }
    }


    //shall never meet here.
    private static void generateService(InsertFileProp fileProp, String daoPackageName, PsiClass srcClass, String servicePackageName) {

    }

    private static void generateMapperXml(InsertFileProp fileProp, PsiClass srcClass, String daoPackageName, String tableName, String primaryKey) {

    }

    private static void generateDaoFile(InsertFileProp fileProp, String daoPackageName, PsiClass srcClass) {

    }

    private static void generateSqlFile(InsertFileProp prop, List<GenCodeProp> propList, String primaryKey, String tableName) {
        List<String> retList = Lists.newArrayList();
        String newTableName = GenCodeUtil.wrapComma(tableName);
        retList.add(String.format("-- auto Generated on %s ", DateUtil.formatLong(new Date())));
        retList.add("-- DROP TABLE IF EXISTS " + newTableName + "; ");
        retList.add("CREATE TABLE " + newTableName + "(");
        for (GenCodeProp field : propList) {
            String fieldSql = genfieldSql(field);
            retList.add(fieldSql);
        }
        // TODO: 2016/12/26 InnoDb and utf8 can be later configured
        retList.add(GenCodeUtil.ONE_RETRACT + "PRIMARY KEY (" + GenCodeUtil.wrapComma(primaryKey) + ")");
        retList.add(String.format(")ENGINE=%s DEFAULT CHARSET=%s COMMENT '%s';", "InnoDB",
                "utf8", newTableName));

        try {
            String filePath = prop.getPath() + "/" + prop.getName();
            Files.write(Paths.get(filePath), retList, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException("can't write file " + prop.getName() + " to path " + prop.getPath() + "/" + prop.getName());
        }
        //then go write to the file.
    }

    private static String genfieldSql(GenCodeProp field) {
        StringBuilder ret = new StringBuilder();
        ret.append(GenCodeUtil.ONE_RETRACT).append(GenCodeUtil.wrapComma(field.getColumnName()))
                .append(" ").append(field.getFiledType());
        if (StringUtils.isNotBlank(field.getSize())) {
            ret.append(" (" + field.getSize() + ")");
        }
        if (field.getUnique()) {
            ret.append(" UNIQUE");
        }
        if (!field.getCanBeNull()) {
            ret.append(" NOT NULL");
        }

        if (StringUtils.isNotBlank(field.getDefaultValue())) {
            ret.append(" DEFAULT " + field.getDefaultValue());
        }

        if (field.getPrimaryKey()) {
            ret.append(" AUTO_INCREMENT");
        }

        ret.append(" COMMENT '" + field.getFieldName() + "'");

        return ret.toString();
    }
}
