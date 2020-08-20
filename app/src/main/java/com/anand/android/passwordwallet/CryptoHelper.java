package com.anand.android.passwordwallet;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class CryptoHelper {
    String AES = "AES";

    String encrypt(String Data, String password) throws Exception {
        SecretKeySpec key = generateKey(password);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = cipher.doFinal(Data.getBytes());
        return Base64.encodeToString(encVal, Base64.DEFAULT);
    }

    private SecretKeySpec generateKey(String password) throws Exception {
        final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes(StandardCharsets.UTF_8);
        messageDigest.update(bytes, 0, bytes.length);
        byte[] key = messageDigest.digest();
        return new SecretKeySpec(key, "AES");
    }

    String decrypt(String outputString, String password) throws Exception {
        SecretKeySpec key = generateKey(password);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodeValue = Base64.decode(outputString, Base64.DEFAULT);
        byte[] decVal = cipher.doFinal(decodeValue);
        return new String(decVal);
    }
}
