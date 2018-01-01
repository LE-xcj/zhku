package com.example.zhkuapp.utils;

import android.util.Log;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by chujian on 2018/1/1.
 */

public class MD5Util {
    private final static String TYPE = "MD5";

    public static String MD5Encode(String str){
        try {
            MessageDigest msgd = MessageDigest.getInstance(TYPE);
            msgd.update(str.getBytes());

            return new BigInteger(msgd.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
