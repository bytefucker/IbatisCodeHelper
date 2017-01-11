package com.ccnode.codegenerator.freemarker;

import com.google.common.collect.Maps;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
        FileWriter fileWriter = new FileWriter("boutMapper.java");
        template.process(rootMap, fileWriter);

    }
}
