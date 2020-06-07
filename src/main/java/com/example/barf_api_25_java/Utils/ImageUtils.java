package com.example.barf_api_25_java.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ImageUtils {
    public static String bitmapToString(Bitmap bmp) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, os);
        byte[] bytes = os.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public static byte[] stringToBitmap(String str) {
        return Base64.decode(str, Base64.DEFAULT);
    }

    public static Bitmap byteToBitmap(byte[] byteArray) {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }
}
