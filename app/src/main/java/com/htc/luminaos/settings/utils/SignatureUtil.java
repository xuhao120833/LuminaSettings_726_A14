package com.htc.luminaos.settings.utils;
/**
 * @author  作者：zgr
 * @version 创建时间：2017年7月4日 下午3:35:31
 * 类说明
 */

import android.util.Log;

import com.htc.luminaos.utils.LogUtils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by gordon.bi on 2016/9/18.
 */
public class SignatureUtil {
    private final static String TAG = "PushS.SignatureUtil";

    public static String getSign(Map<String, String> map) {
        StringBuffer sbf = new StringBuffer();
        String[] arr = map.keySet().toArray(new String[0]);
        Arrays.sort(arr);//sort by dictionary
        for (String key : arr) {
            if (!key.equals("")) {
                String value = map.get(key);
                if (value == null) {
                    value = "";
                }
                sbf.append(key).append(value);
            }
        }
        String sign = null;
        try {
            sign = new String(Hex.encodeHex(DigestUtils.sha1(sbf.toString().getBytes()))).toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.d(TAG,"sign:"+sign);
        return sign;
    }
    
    
    public static String getSignVersion(Map<String, String> map,int size) {
        StringBuffer sbf = new StringBuffer();
        String[] arr = map.keySet().toArray(new String[0]);
        Arrays.sort(arr);//sort by dictionary
        for (String key : arr) {
            if (!key.equals("")) {
                String value = map.get(key);
                if (value == null) {
                    value = "";
                }
                sbf.append(key).append(value);
            }
        }
        
        sbf.append(size);
        
        String sign = null;
        try {
            sign = new String(Hex.encodeHex(DigestUtils.sha1(sbf.toString().getBytes()))).toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.d(TAG,"sign:"+sign);
        return sign;
    }
    
    /**
     * get file md5
     * @param file
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public static String getFileMD5(File file) throws NoSuchAlgorithmException, IOException {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest;
        FileInputStream in;
        byte buffer[] = new byte[1024];
        int len;
        digest = MessageDigest.getInstance("MD5");
        in = new FileInputStream(file);
        while ((len = in.read(buffer, 0, 1024)) != -1) {
            digest.update(buffer, 0, len);
        }
        in.close();
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }
    
    // 计算文件的 MD5 值
    public static String md5(File file) {
        if (file == null || !file.isFile() || !file.exists()) {
            return "";
        }
        FileInputStream in = null;
        String result = "";
        byte buffer[] = new byte[8192];
        int len;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer)) != -1) {
                md5.update(buffer, 0, len);
            }
            byte[] bytes = md5.digest();

            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(null!=in){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }


}