package com.ccnode.codegenerator.freemarker;

import com.ccnode.codegenerator.dialog.dto.mybatis.ColumnAndField;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.junit.Test;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * @Author bruce.ge
 * @Date 2017/1/11
 * @Description
 */
public class GenerateMapperTest {
    @Test
    public void testGenerateMapper() throws IOException, TemplateException {
        Configuration configuration = new Configuration(Configuration.getVersion());
        configuration.setDirectoryForTemplateLoading(new File(GenerateMapperTest.class.getClassLoader().getResource("templates").getPath()));
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setLogTemplateExceptions(false);
        Map<String, String> rootMap = Maps.newHashMap();
        rootMap.put("packageName", "com.rest.mapper");
        rootMap.put("daoname", "aboutMapper");
        rootMap.put("beanFullType", "com.rest.domain.aboutPO");
        rootMap.put("beanShortType", "aboutPO");

        Template template = configuration.getTemplate("mapperdao.ftl");
//        FileWriter fileWriter = new FileWriter("boutMapper.java");
        StringWriter stringWriter = new StringWriter();
        template.process(rootMap, stringWriter);
        System.out.println(stringWriter.toString());
        System.out.println("nimei");

    }

    @Test
    public void testGenAddMethod() throws IOException, TemplateException {
        List<ColumnAndField> ss = Lists.newArrayList();
        for (int i = 0; i < 5; i++) {
            ColumnAndField e = new ColumnAndField();
            e.setField(i + "hello");
            e.setColumn("hehe" + i);
            ss.add(e);
        }
        String tableName = "nima";
        Map<String, Object> root = Maps.newHashMap();
        root.put("tableName", tableName);
        root.put("finalFields", ss);
        Configuration configuration = new Configuration(Configuration.getVersion());
        configuration.setDirectoryForTemplateLoading(new File(GenerateMapperTest.class.getClassLoader().getResource("templates").getPath()));
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setLogTemplateExceptions(false);
        Template template = configuration.getTemplate("insert.ftl");
        template.process(root, new OutputStreamWriter(System.out));
    }
}
