package com.yoryz.netty.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

/**
 * TODO
 *
 * @author Yory
 * @version 1.0
 * @date 2019/5/16 17:27
 */
public class EncoderUtils {

    public static String getMD5Str(String strValue) throws NoSuchAlgorithmException {
        return Base64.encodeBase64String(MessageDigest.getInstance("MD5").digest(strValue.getBytes()));
    }
}
