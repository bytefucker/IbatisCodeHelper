package com.ccnode.codegenerator.service.pojo;

import com.ccnode.codegenerator.dialog.GenCodeProp;
import com.ccnode.codegenerator.dialog.InsertDialogResult;
import com.ccnode.codegenerator.dialog.InsertFileProp;
import com.intellij.psi.PsiClass;

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

    }
}
