package com.pixel.hexagonmatchsdk;
import android.content.Context;
import android.provider.Settings.Secure;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The MIT License (MIT)
 *
 *  Copyright (c) 2020 Hexagon Data
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * *******************************************************************************
 */
public class Utils {
    public static String getUuid(Context context) {
        String udid =  Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        return sha1(udid);
    }

    public static String sha1(String source) {
        if (source == null) {
            return null;
        }

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");

        } catch (NoSuchAlgorithmException e) {
            return null;
        }

        md.update(source.getBytes());
        StringBuilder builder = new StringBuilder();
        byte[] bytes = md.digest();
        for (int i = 0; i < bytes.length; i++) {
            builder.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return builder.toString();
    }

    public static String sha256(String source) {
        if (source == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(source.getBytes("UTF8"));
            for (int i = 0; i < bytes.length; i++) {
                builder.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }

        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        return builder.toString();
    }

    /**
     *
     * @param email
     * @return
     */
    public static String normalizeEmail(String email) {
        return email.trim().toLowerCase().replaceFirst("\\+[^@]*@", "@");
    }

    /**
     *
     * @param phone
     * @return
     */
    public static String normalizePhone(String phone) {
        String result = phone;
        try {
            result = result.trim().replaceAll("/^[0]+", "").replaceAll("\\D", "");
        } catch (Exception e) {

        }
        return result;
    }

    public static String getKeyType(String key) {
        String keyTypeString = "";
        switch (key)
        {
            case "email":
                keyTypeString = "&e=";
                break;
            case "mobile":
                keyTypeString = "&mo=";
                break;
            case "customer":
                keyTypeString = "&cu=";
                break;
        }

        return keyTypeString;
    }
}