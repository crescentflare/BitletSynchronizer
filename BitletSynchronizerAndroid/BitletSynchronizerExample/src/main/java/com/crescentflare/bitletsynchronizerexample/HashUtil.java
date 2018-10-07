package com.crescentflare.bitletsynchronizerexample;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * An easy way to generate hashing results, like MD5
 */
public class HashUtil
{
    public static String generateMD5(String text)
    {
        MessageDigest messageDigest = null;
        try
        {
            messageDigest = MessageDigest.getInstance("MD5");
            final byte[] digest = messageDigest.digest(text.getBytes());
            return bytesToHex(digest);
        }
        catch (NoSuchAlgorithmException ignored)
        {
        }
        return "unknown";
    }

    private static String bytesToHex(byte[] bytes)
    {
        final char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++)
        {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
