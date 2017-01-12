package com.ccnode.codegenerator.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.*;
import java.util.Map;

/**
 * @Author bruce.ge
 * @Date 2017/1/12
 * @Description
 */
public class TemplateUtil {

    private static Configuration configuration;


    static {
        configuration = new Configuration(Configuration.getVersion());
        try {
            configuration.setDirectoryForTemplateLoading(new File(TemplateUtil.class.getClassLoader().getResource("templates").getPath()));
        } catch (IOException e) {

            e.printStackTrace();
        }
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setLogTemplateExceptions(false);
    }

    public static String processToString(String templateName, Map<String, Object> root) {
        try {
            Template template = configuration.getTemplate(templateName);
            StringWriter out = new StringWriter();
            template.process(root, out);
            return out.toString();
        } catch (Exception e) {
            // TODO: 2017/1/12 add logger
            return null;
        }
    }
}
