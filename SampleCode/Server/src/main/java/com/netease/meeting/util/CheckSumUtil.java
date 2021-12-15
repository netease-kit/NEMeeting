package com.netease.meeting.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CheckSumUtil {
    private static final Log log = LogFactory.getLog(CheckSumUtil.class);

    private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    public static String getCheckSum(String nonce, String curTime, String appSecret) {
        String plaintext = appSecret + nonce + curTime;
        return encode(plaintext, "SHA1");
    }

    public static String getCheckSum(String nonce, String curTime, String appSecret, String data) {
        String plaintext = appSecret + nonce + curTime + data;
        return encode(plaintext, "SHA1");
    }

    // 计算并获取md5值
    public static String getMD5(String requestBody) {
        return encode(requestBody, "md5");
    }

    private static String encode(String plaintext, String method) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(method);
            messageDigest.update(plaintext.getBytes(Charset.forName("UTF-8")));
            return getFormattedText(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            log.error("Encode error "+plaintext,e);
        }

        return "";
    }

    /**
     * Takes the raw bytes from the digest and formats them correct.
     *
     * @param bytes
     *            the raw bytes from the digest.
     * @return the formatted bytes.
     */
    protected static String getFormattedText(byte[] bytes) {
        int len = bytes.length;
        StringBuilder buf = new StringBuilder(len * 2);
        // 把密文转换成十六进制的字符串形式
        for (byte aByte : bytes) {
            buf.append(HEX_DIGITS[(aByte >> 4) & 0x0f]);
            buf.append(HEX_DIGITS[aByte & 0x0f]);
        }
        return buf.toString();
    }
}

