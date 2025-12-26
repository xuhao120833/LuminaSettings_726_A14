package com.htc.luminasettings.manager;

import com.htc.luminasettings.utils.LogUtils;
import com.htc.luminasettings.utils.VerifyUtil;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class RequestManager {

    private static final RequestManager requestManager = new RequestManager();

    public static RequestManager getInstance(){
        return requestManager;
    }

    public  Call getData(String url, Callback callback){
        LogUtils.d("url="+url);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder().url(url);
        Call call = okHttpClient.newCall(requestBuilder.build());
        call.enqueue(callback);
        return call;
    }

    public String getSign(String body,String chanId,String time){
        String stringBuilder = "body" + body +
                "chanId" + chanId +
                "chanKey" + VerifyUtil.KEY +
                "timestamp" + time;
        return VerifyUtil.sha1(stringBuilder);
    }

    public  static boolean isOne(int num , int n){
        return (num >> (n - 1) & 1) == 1;
    }

}
