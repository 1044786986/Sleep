package com.example.ljh.sleep.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncodeUtils {
    private static final String KEY_SHA = "SHA";

    public static String ShaEncode(String data){
        BigInteger bigInteger = null;
        try {
            byte dataBytes[] = data.getBytes();
            MessageDigest messageDigest = MessageDigest.getInstance(KEY_SHA);
            messageDigest.update(dataBytes);
            bigInteger = new BigInteger(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return bigInteger+"";
    }
}
