package com.htc.luminasettings.utils;

public interface Uri {

    int complexType = 7;//按位进行复杂度调试，从右往左按位匹配第1位:表示开启请求参数deviceld的解密验证第2位:表示开启返回值下载地址的加密第3位:表示开启返回值body的压缩
    String BASE_URL = "http://store-api.aodintech.com";
    //test 环境
    //String BASE_URL = "http://150.158.81.120:8601";
    String SIGN_APP_LIST_URL = BASE_URL+"/sign/app/list";
    String APP_LIST_URL = BASE_URL+"/app/list";
    String SIGN_REPORT_URL = BASE_URL+"/app/install/report";
    String REPORT_URL = BASE_URL+"/sign/app/install/report";

    String SIGN_CHECK_UPDATE_URL = BASE_URL+"/sign/app/update/check";
    String CHECK_UPDATE_URL = BASE_URL+"/app/update/check";



}
