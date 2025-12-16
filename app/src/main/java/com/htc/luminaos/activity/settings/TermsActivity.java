package com.htc.luminaos.activity.settings;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.core.text.HtmlCompat;

import com.htc.luminaos.R;
import com.htc.luminaos.activity.BaseActivity;
import com.htc.luminaos.databinding.ActivityTermsBinding;
import com.htc.luminaos.databinding.InitAngleLayoutBinding;
import com.htc.luminaos.utils.KeystoneUtils_726;
import com.htc.luminaos.utils.LogUtils;
import com.htc.luminaos.utils.ReflectUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

public class TermsActivity extends BaseActivity {
    private static String TAG = "TermsActivity";
    private String type = "";
    private ActivityTermsBinding termsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        if (type.isEmpty())
            return;
        LogUtils.d(TAG, " loadTerms type " + type);
        termsBinding = ActivityTermsBinding.inflate(LayoutInflater.from(getApplicationContext()));
        setContentView(termsBinding.getRoot());
        initView();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    private void initView() {
        loadTerms();
    }

    private void loadTerms() {
        String lang = Locale.getDefault().getLanguage();
        String country = Locale.getDefault().getCountry();
        LogUtils.d(TAG, "loadTerms lang " + lang + " " + country);
        String fileName = "";
        if ("terms".equals(type)) {
            if ("zh".equals(lang) && !"CN".equals(country)) {
                fileName = "html/terms/terms_" + lang + country + ".html";
            } else {
                fileName = "html/terms/terms_" + lang + ".html";
            }
        } else if ("privacy".equals(type)) {
            if ("zh".equals(lang)&& !"CN".equals(country)) {
                fileName = "html/privacy/privacy_" + lang + country + ".html";
            } else {
                fileName = "html/privacy/privacy_" + lang + ".html";
            }
        }
        LogUtils.d(TAG, "loadTerms fileName " + fileName);

        InputStream is = null;
        try {
            is = getAssets().open(fileName);
        } catch (Exception e) {
            // 文件不存在，回退到英文版本
            LogUtils.d(TAG, "File not found: " + fileName + ", fallback to English");
            try {
                if ("terms".equals(type)) {
                    is = getAssets().open("html/terms/terms_en.html");
                } else if ("privacy".equals(type)) {
                    is = getAssets().open("html/privacy/privacy_en.html");
                }
            } catch (Exception d) {
                d.printStackTrace();
            }
        }
        if (is != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                StringBuilder sb = new StringBuilder();
                String line;
                boolean inBody = false;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.contains("<body")) {
                        inBody = true;
                        line = line.substring(line.indexOf(">") + 1);
                    }
                    if (line.contains("</body>")) {
                        line = line.substring(0, line.indexOf("</body>"));
                        inBody = false;
                    }
                    if (inBody || (!line.isEmpty() && line.startsWith("<"))) {
                        sb.append(line);
                    }
                }
                termsBinding.tvTerms.setText(HtmlCompat.fromHtml(sb.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}