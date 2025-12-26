package com.htc.luminasettings.bean;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * 读取 /system 目录下的配置文件
 * */
public class ConfigInfo {
    private Properties properties;
    private String PATH = "/system/etc/";

    public ConfigInfo(String fileName){
        readSystemProperties(fileName);
    }
    /**
     * 读取 /system 目录下的配置文件
     * */
    public Properties readSystemProperties(String filename) {
        if(filename == null){
            return null;
        }
        if(!filename.startsWith(File.separator)){
            filename = File.separator + filename;
        }
        InputStream inputStream = null;
        try {
            filename = PATH + filename;

            properties = new Properties();
            inputStream = new BufferedInputStream(new FileInputStream(filename));
            properties.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(inputStream != null){
                try {
                    inputStream.close();
                }catch (IOException ioException){
                    ioException.printStackTrace();
                }
            }
        }
        return properties;
    }

    public String getString(String key) {
        if(properties == null){
            return "";
        }
        if (!properties.containsKey(key)) {
            return "";
        }
        return String.valueOf(properties.get(key));
    }

}
