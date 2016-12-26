package com.ccnode.codegenerator.service.pojo;

import com.ccnode.codegenerator.dialog.InsertDialogResult;
import com.ccnode.codegenerator.dialog.InsertFileProp;
import com.ccnode.codegenerator.dialog.InsertFileType;
import com.ccnode.codegenerator.genCode.GenDaoService;
import com.ccnode.codegenerator.genCode.GenMapperService;
import com.ccnode.codegenerator.genCode.GenServiceService;
import com.ccnode.codegenerator.genCode.GenSqlService;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by bruce.ge on 2016/12/26.
 */
public class GenerateInsertCodeService {

    public static void generateInsert(InsertDialogResult insertDialogResult) {
        Map<InsertFileType, InsertFileProp> fileProps = insertDialogResult.getFileProps();
        ExecutorService executorService = Executors.newFixedThreadPool(fileProps.size());
        CountDownLatch latch = new CountDownLatch(fileProps.size());
        for (InsertFileType fileType : fileProps.keySet()) {
            executorService.submit(() -> {
                try {
                    generateFiles(fileType, fileProps, insertDialogResult);
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


    private static void generateFiles(InsertFileType type, Map<InsertFileType, InsertFileProp> propMap, InsertDialogResult insertDialogResult) {
        switch (type) {
            case SQL: {
                GenSqlService.generateSqlFile(propMap.get(type), insertDialogResult.getPropList(), insertDialogResult.getPrimaryKey(), insertDialogResult.getTableName());
                break;
            }
            case DAO: {
                GenDaoService.generateDaoFile(propMap.get(type), insertDialogResult.getSrcClass());
                break;
            }
            case MAPPER_XML: {
                GenMapperService.generateMapperXml(propMap.get(type), insertDialogResult.getPropList(), insertDialogResult.getSrcClass(), propMap.get(InsertFileType.DAO), insertDialogResult.getTableName(), insertDialogResult.getPrimaryKey());
                break;
            }
            case SERVICE: {
                GenServiceService.generateService(propMap.get(type), insertDialogResult.getSrcClass(), propMap.get(InsertFileType.DAO));
                break;
            }
        }
    }


}
