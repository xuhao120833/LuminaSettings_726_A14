package com.htc.luminasettings.utils;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Map;

public class GenerateQrBitmap {

    private static final String TAG = "GenerateQrBitmap";

    public static Bitmap createQrBitmap(String rawUrl, int sizePx) {
        if (rawUrl == null) {
            LogUtils.e(TAG, "url == null");
            return null;
        }
        String url = rawUrl.trim();
        if (url.isEmpty()) {
            LogUtils.e(TAG, "url is empty after trim");
            return null;
        }
        if (!(url.startsWith("http://") || url.startsWith("https://"))) {
            LogUtils.e(TAG, "url must start with http/https: " + url);
            return null;
        }
        if (sizePx <= 0) {
            LogUtils.e(TAG, "sizePx <= 0, given: " + sizePx);
            return null;
        }
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            BitMatrix matrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, sizePx, sizePx, hints);
            int w = matrix.getWidth();
            int h = matrix.getHeight();
            int[] pixels = new int[w * h];
            for (int y = 0; y < h; y++) {
                int offset = y * w;
                for (int x = 0; x < w; x++) {
                    pixels[offset + x] = matrix.get(x, y) ? Color.BLACK : Color.WHITE;
                }
            }
            Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bmp.setPixels(pixels, 0, w, 0, 0, w, h);
            LogUtils.d(TAG," 返回生成的bitmap "+bmp);
            return bmp;
        } catch (WriterException we) {
            LogUtils.e(TAG, "WriterException: " + we.getMessage());
            return null;
        } catch (Throwable t) { // 包含 OOM 等
            LogUtils.e(TAG, "Unexpected error: " + t.getMessage());
            return null;
        }
    }

}
