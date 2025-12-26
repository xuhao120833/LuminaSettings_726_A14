package com.htc.luminasettings.utils;

import static com.htc.luminasettings.utils.Utils.FAQ;
import static com.htc.luminasettings.utils.Utils.QUICK_GUID;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.htc.luminasettings.R;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FaqGuideUtils {

    private static String TAG = "FaqGuideUtils";

    public static void checkAndOpenUrls(String faqBaseUrl, String guideBaseUrl, Context context) {
        try {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                Locale currentLocale = Locale.getDefault();
                String languageCode = currentLocale.getLanguage();

                String validFaqUrl = null;
                String validGuideUrl = null;

                // ---------- FAQ ----------
                if (!TextUtils.isEmpty(faqBaseUrl) && faqBaseUrl.equals(FAQ)) {
                    String faqUrl = faqBaseUrl + languageCode + "/faq.html";
                    String fallbackFaqUrl = faqBaseUrl + "en/faq.html";

                    if (checkUrlExists(faqUrl)) {
                        LogUtils.d(TAG, "faqUrl existed: " + faqUrl);
                        validFaqUrl = faqUrl;
                    } else {
                        LogUtils.d(TAG, "faqUrl invalid, use fallback: " + fallbackFaqUrl);
                        validFaqUrl = fallbackFaqUrl;
                    }
                } else {
                    if (checkUrlExists(faqBaseUrl)) {
                        LogUtils.d(TAG, "faqBaseUrl existed: " + faqBaseUrl);
                        validFaqUrl = faqBaseUrl;
                    }
                }

                // ---------- Quick Guide ----------
                if (!TextUtils.isEmpty(guideBaseUrl) && guideBaseUrl.equals(QUICK_GUID)) {
                    String guideUrl = guideBaseUrl + languageCode + "/manual.pdf";
                    String fallbackGuideUrl = guideBaseUrl + "en/manual.pdf";

                    if (checkUrlExists(guideUrl)) {
                        LogUtils.d(TAG, "guideUrl existed: " + guideUrl);
                        validGuideUrl = guideUrl;
                    } else {
                        LogUtils.d(TAG, "guideUrl invalid, use fallback: " + fallbackGuideUrl);
                        validGuideUrl = fallbackGuideUrl;
                    }
                } else {
                    if (checkUrlExists(guideBaseUrl)) {
                        LogUtils.d(TAG, "guideBaseUrl existed: " + guideBaseUrl);
                        validGuideUrl = guideBaseUrl;
                    }
                }

                String finalFaqUrl = validFaqUrl;
                String finalGuideUrl = validGuideUrl;

                handler.post(() -> {
                    if (finalFaqUrl != null || finalGuideUrl != null) {
                        createQrDialog(finalFaqUrl, finalGuideUrl, context);
                    } else {
                        Toast.makeText(context, "无法获取帮助链接", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean checkUrlExists(String urlString) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(2000);
            int responseCode = connection.getResponseCode();
            return (responseCode == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static void createQrDialog(String faqUrl, String guideUrl, Context context) {
        try {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.qr_dialog);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setGravity(Gravity.CENTER);

            LinearLayout container = dialog.findViewById(R.id.qr_container);
            container.removeAllViews();

            // 动态添加二维码视图
            if (!TextUtils.isEmpty(guideUrl)) {
                View guideView = LayoutInflater.from(context).inflate(R.layout.item_qr, container, false);
                setupQrItem(guideView, context.getString(R.string.quick_guide), guideUrl, context);
                container.addView(guideView);
            }

            if (!TextUtils.isEmpty(faqUrl)) {
                View faqView = LayoutInflater.from(context).inflate(R.layout.item_qr, container, false);
                setupQrItem(faqView, context.getString(R.string.faq), faqUrl, context);
                container.addView(faqView);
            }

            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成单个二维码项（包含标题和二维码）
     */
    public static void setupQrItem(View view, String title, String url, Context context) {
        TextView titleView = view.findViewById(R.id.qr_title);
        ImageView imageView = view.findViewById(R.id.qr_image);

        titleView.setText(title);
        titleView.setSelected(true);
        if (!TextUtils.isEmpty(url)) {
            Bitmap bitmap = GenerateQrBitmap.createQrBitmap(url, context.getResources().getDimensionPixelSize(R.dimen.y_300));
            imageView.setImageBitmap(bitmap);
        }
    }
}
