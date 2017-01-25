package com.ccnode.codegenerator.genCode;

import com.ccnode.codegenerator.dialog.InsertFileProp;
import com.ccnode.codegenerator.enums.FileType;
import com.ccnode.codegenerator.enums.MethodName;
import com.ccnode.codegenerator.pojo.ClassInfo;
import com.ccnode.codegenerator.pojo.GenCodeResponse;
import com.ccnode.codegenerator.pojo.GeneratedFile;
import com.ccnode.codegenerator.pojo.OnePojoInfo;
import com.ccnode.codegenerator.pojoHelper.GenCodeResponseHelper;
import com.ccnode.codegenerator.util.GenCodeUtil;
import com.ccnode.codegenerator.util.LoggerWrapper;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * What always stop you is what you always believe.
 * <p>
 * Created by zhengjun.du on 2016/05/28 21:14
 */
public class GenServiceService {

    private final static Logger LOGGER = LoggerWrapper.getLogger(GenServiceService.class);

    public static void genService(GenCodeResponse response) {
        for (OnePojoInfo pojoInfo : response.getPojoInfos()) {
            try {
                GeneratedFile fileInfo = GenCodeResponseHelper.getByFileType(pojoInfo, FileType.SERVICE);
                Boolean useGenericDao = Objects.equal(response.getUserConfigMap().get("usegenericdao"), "true");
                genDaoFile(pojoInfo, fileInfo, useGenericDao);

            } catch (Throwable e) {
                LOGGER.error("GenServiceService genService error", e);
                response.failure("GenServiceService genService error");
            }
        }
    }

    private static void genDaoFile(OnePojoInfo onePojoInfo, GeneratedFile fileInfo, Boolean useGenericDao) {
        String pojoName = onePojoInfo.getPojoName();
        String pojoNameDao = pojoName + "Dao";
        if (!fileInfo.getOldLines().isEmpty()) {
            fileInfo.setNewLines(fileInfo.getOldLines());
            return;
        }
        if (useGenericDao) {
            List<String> newLines = Lists.newArrayList();
            newLines.add("package " + onePojoInfo.getServicePackage() + ";");
            newLines.add("");
            newLines.add("import org.springframework.stereotype.Service;");
            newLines.add("import javax.annotation.Resource;");
            newLines.add("import java.util.List;");
            newLines.add("import " + onePojoInfo.getPojoPackage() + "." + onePojoInfo.getPojoName() + ";");
            newLines.add("import " + onePojoInfo.getDaoPackage() + "." + onePojoInfo.getPojoName() + "Dao;");
            newLines.add("");
            newLines.add("@Service");
            newLines.add("public class " + pojoName + "Service extends GenericService<" + pojoName + "> {");
            newLines.add("");
            newLines.add("    @Resource");
            newLines.add(
                    GenCodeUtil.ONE_RETRACT + "private " + pojoName + "Dao " + GenCodeUtil.getLowerCamel(pojoName) + "Dao;");
            newLines.add("");
            newLines.add("    @Override");
            newLines.add(GenCodeUtil.ONE_RETRACT + "public GenericDao<" + pojoName + "> getGenericDao() {");
            newLines.add(GenCodeUtil.TWO_RETRACT + "return " + GenCodeUtil.getLowerCamel(pojoNameDao) + ";");
            newLines.add(GenCodeUtil.ONE_RETRACT + "}");
            newLines.add("}");
            fileInfo.setNewLines(newLines);
        } else {
            List<String> newLines = Lists.newArrayList();
            String daoName = GenCodeUtil.getLowerCamel(pojoName) + "Dao";
            newLines.add("package " + onePojoInfo.getServicePackage() + ";");
            newLines.add("");
            newLines.add("import org.springframework.stereotype.Service;");
            newLines.add("import javax.annotation.Resource;");
            newLines.add("import java.util.List;");
            newLines.add("import " + onePojoInfo.getPojoPackage() + "." + onePojoInfo.getPojoName() + ";");
            newLines.add("import " + onePojoInfo.getDaoPackage() + "." + onePojoInfo.getPojoName() + "Dao;");
            newLines.add("");
            newLines.add("@Service");
            newLines.add("public class " + pojoName + "Service {");
            newLines.add("");
            newLines.add("    @Resource");
            newLines.add(GenCodeUtil.ONE_RETRACT + "private " + pojoName + "Dao " + daoName + ";");
            newLines.add("");
            newLines.add(GenCodeUtil.ONE_RETRACT + "public int " + MethodName.insert.name() + "(" + pojoName + " pojo){");
            newLines.add(GenCodeUtil.TWO_RETRACT + "return " + daoName + "." + MethodName.insert.name() + "(pojo);");
            newLines.add(GenCodeUtil.ONE_RETRACT + "}");
            newLines.add("");
            newLines.add(GenCodeUtil.ONE_RETRACT + "public int " + MethodName.insertList.name() + "(List< " + pojoName + "> pojos){");
            newLines.add(GenCodeUtil.TWO_RETRACT + "return " + daoName + "." + MethodName.insertList.name() + "(pojos);");
            newLines.add(GenCodeUtil.ONE_RETRACT + "}");
            newLines.add("");
//            newLines.add(
//                    GenCodeUtil.ONE_RETRACT + "public List<"+pojoName+"> "+ MethodName.select.name() +"("+pojoName +" pojo){");
//            newLines.add(GenCodeUtil.TWO_RETRACT + "return "+daoName + "."+ MethodName.select.name() +"(pojo);");
//            newLines.add(GenCodeUtil.ONE_RETRACT + "}");
//            newLines.add("");
            newLines.add(GenCodeUtil.ONE_RETRACT + "public int " + MethodName.update.name() + "(" + pojoName + " pojo){");
            newLines.add(GenCodeUtil.TWO_RETRACT + "return " + daoName + "." + MethodName.update.name() + "(pojo);");
            newLines.add(GenCodeUtil.ONE_RETRACT + "}");
            newLines.add("");
            newLines.add("}");
            fileInfo.setNewLines(newLines);
        }

    }

    //shall never meet here.
    public static void generateService(InsertFileProp fileProp, ClassInfo srcClass, InsertFileProp daoProp) {
        List<String> newLines = Lists.newArrayList();
        String daoName = GenCodeUtil.getLowerCamel(daoProp.getName());
        newLines.add("package " + fileProp.getPackageName() + ";");
        newLines.add("");
        newLines.add("import org.springframework.stereotype.Service;");
        newLines.add("import javax.annotation.Resource;");
        newLines.add("import java.util.List;");
        newLines.add("import " + srcClass.getQualifiedName() + ";");
        newLines.add("import " + daoProp.getQutifiedName() + ";");
        newLines.add("");
        newLines.add("@Service");
        newLines.add("public class " + fileProp.getName() + "{");
        newLines.add("");
        newLines.add("    @Resource");
        newLines.add(GenCodeUtil.ONE_RETRACT + "private " + daoProp.getName() + " " + daoName + ";");
        newLines.add("");
        newLines.add(GenCodeUtil.ONE_RETRACT + "public int " + MethodName.insert.name() + "(" + srcClass.getName() + " pojo){");
        newLines.add(GenCodeUtil.TWO_RETRACT + "return " + daoName + "." + MethodName.insert.name() + "(pojo);");
        newLines.add(GenCodeUtil.ONE_RETRACT + "}");
        newLines.add("");
        newLines.add(GenCodeUtil.ONE_RETRACT + "public int " + MethodName.insertList.name() + "(List<" + srcClass.getName() + "> pojos){");
        newLines.add(GenCodeUtil.TWO_RETRACT + "return " + daoName + "." + MethodName.insertList.name() + "(pojos);");
        newLines.add(GenCodeUtil.ONE_RETRACT + "}");
        newLines.add("");
//            newLines.add(
//                    GenCodeUtil.ONE_RETRACT + "public List<"+pojoName+"> "+ MethodName.select.name() +"("+pojoName +" pojo){");
//            newLines.add(GenCodeUtil.TWO_RETRACT + "return "+daoName + "."+ MethodName.select.name() +"(pojo);");
//            newLines.add(GenCodeUtil.ONE_RETRACT + "}");
//            newLines.add("");
        newLines.add(GenCodeUtil.ONE_RETRACT + "public int " + MethodName.update.name() + "(" + srcClass.getName() + " pojo){");
        newLines.add(GenCodeUtil.TWO_RETRACT + "return " + daoName + "." + MethodName.update.name() + "(pojo);");
        newLines.add(GenCodeUtil.ONE_RETRACT + "}");
        newLines.add("");
        newLines.add("}");

        try {
            String filePath = fileProp.getFullPath();
            Files.write(Paths.get(filePath), newLines, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException("can't write file " + fileProp.getName() + " to path " + fileProp.getFullPath());
        }
    }
}
