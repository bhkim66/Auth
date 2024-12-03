package com.bhkim.auth.util;

import com.bhkim.auth.exception.ApiException;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.util.Base64;

import static com.bhkim.auth.exception.ExceptionEnum.INTERNAL_SERVER_ERROR;


@Slf4j
public class AESUtil {
    public static String alg = "AES/CBC/PKCS5Padding";
//    @Value("${privateKey.aes}")
    private static String key = "5nskpnps20ghlk5kcBLEK3Lb02lg5aa7";

//    private static final String key = "5nskpnps20ghlk5kcBLEK3Lb02lg5aa7";
//    private String IV = key.substring(0, 16);

    public static String encrypt(String text) {
        byte[] encrypted = null;
        try {
            Cipher cipher = Cipher.getInstance(alg);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivParamSpec = new IvParameterSpec(key.substring(0, 16).getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);

            encrypted = cipher.doFinal(text.getBytes("UTF-8"));
        } catch (Exception e) {
            log.error("e : {}" , e.getMessage());
            throw new ApiException(INTERNAL_SERVER_ERROR);
        }
//        return Base64.getEncoder().encodeToString(encrypted);
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String urlEncrypt(String text) {
        byte[] encrypted = null;
        try {
            Cipher cipher = Cipher.getInstance(alg);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivParamSpec = new IvParameterSpec(key.substring(0, 16).getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);

            encrypted = cipher.doFinal(text.getBytes("UTF-8"));
        } catch (Exception e) {
            log.error("e : {}" , e.getMessage());
            throw new ApiException(INTERNAL_SERVER_ERROR);
        }
//        return Base64.getEncoder().encodeToString(encrypted);
        return Base64.getUrlEncoder().encodeToString(encrypted);
    }

    public static String decrypt(String text) throws Exception {
        byte[] decrypted = null;
        Cipher cipher = Cipher.getInstance(alg);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(key.substring(0, 16).getBytes());
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);
        byte[] decodedBytes = Base64.getDecoder().decode(text);
        decrypted = cipher.doFinal(decodedBytes);

        return new String(decrypted, "UTF-8");
    }

    public static String urlDecrypt(String text) throws Exception {
        byte[] decrypted = null;
        Cipher cipher = Cipher.getInstance(alg);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(key.substring(0, 16).getBytes());
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);

        String urlDecodedText = URLDecoder.decode(text, "UTF-8");

        byte[] decodedBytes = Base64.getUrlDecoder().decode(urlDecodedText);
        decrypted = cipher.doFinal(decodedBytes);

        return new String(decrypted, "UTF-8");
    }
}



