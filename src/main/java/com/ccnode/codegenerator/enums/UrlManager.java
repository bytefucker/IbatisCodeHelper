package com.ccnode.codegenerator.enums;

import com.ccnode.codegenerator.common.VersionManager;
import com.ccnode.codegenerator.storage.SettingService;

/**
 * What always stop you is what you always believe.
 * <p>
 * Created by zhengjun.du on 2016/08/31 16:39
 */
public class UrlManager {

    //    private static String GENERATOR_URL = "http://www.codehelper.me/generator/";
    private static String MAIN_PAGE = "https://github.com/gejun123456/MyBatisCodeHelper";

    // TODO: 2017/2/3 need fix?
    private static String POST_URL = "http://www.codehelper.me/generator/post";

    public static final String GENERATOR_URL = "https://github.com/zhengjunbase/MyBatisCodeHelper/tree/develop";

    public static String getUrlSuffix() {
        return "?id=" + SettingService.getUUID() + "&version=" + VersionManager.getCurrentVersion();
    }

    public static String getMainPage() {
        return MAIN_PAGE + getUrlSuffix();
    }


    public static String getPostUrl() {
        return POST_URL + getUrlSuffix();
    }



}
