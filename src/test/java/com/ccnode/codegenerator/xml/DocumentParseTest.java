package com.ccnode.codegenerator.xml;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

/**
 * Created by bruce.ge on 2016/12/29.
 */
public class DocumentParseTest {

    public static final String SQL_TEXT = "INSERT INTO `user_po`\n" +
            "        <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n" +
            "            <if test=\"pojo.id != null\"> `id`, </if>\n" +
            "            <if test=\"pojo.username != null\"> `username`, </if>\n" +
            "            <if test=\"pojo.encryptPassword != null\"> `encrypt_password`, </if>\n" +
            "            <if test=\"pojo.email != null\"> `email`, </if>\n" +
            "            <if test=\"pojo.nimade != null\"> `nimade`, </if>\n" +
            "            <if test=\"pojo.roleId != null\"> `role_id`, </if>\n" +
            "            <if test=\"pojo.addTime != null\"> `add_time`, </if>\n" +
            "            <if test=\"pojo.updateTime != null\"> `update_time`, </if>\n" +
            "            <if test=\"pojo.myOrderId != null\"> `my_order_id`, </if>\n" +
            "            <if test=\"pojo.mobile != null\"> `mobile`, </if>\n" +
            "            <if test=\"pojo.orderId != null\"> `order_id`, </if>\n" +
            "        </trim>\n" +
            "        VALUES\n" +
            "        <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n" +
            "            <if test=\"pojo.id != null\"> #{pojo.id}, </if>\n" +
            "            <if test=\"pojo.username != null\"> #{pojo.username}, </if>\n" +
            "            <if test=\"pojo.encryptPassword != null\"> #{pojo.encryptPassword}, </if>\n" +
            "            <if test=\"pojo.email != null\"> #{pojo.email}, </if>\n" +
            "            <if test=\"pojo.nimade != null\"> #{pojo.nimade}, </if>\n" +
            "            <if test=\"pojo.roleId != null\"> #{pojo.roleId}, </if>\n" +
            "            <if test=\"pojo.addTime != null\"> #{pojo.addTime}, </if>\n" +
            "            <if test=\"pojo.updateTime != null\"> #{pojo.updateTime}, </if>\n" +
            "            <if test=\"pojo.myOrderId != null\"> #{pojo.myOrderId}, </if>\n" +
            "            <if test=\"pojo.mobile != null\"> #{pojo.mobile}, </if>\n" +
            "            <if test=\"pojo.orderId != null\"> #{pojo.orderId}, </if>\n" +
            "        </trim>";

    @Test
    public void test() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document parse = documentBuilder.parse(new InputSource(new StringReader(SQL_TEXT)));
            Element element =
                    parse.getDocumentElement();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testSplit(){
        String[] split = SQL_TEXT.split("(\\s|\n|\t)+");
        System.out.println(split.length);
        for (int i = 0; i < split.length; i++) {
            System.out.println(split[i]);
        }
    }
}
