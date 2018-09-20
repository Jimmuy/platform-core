package com.jimmy.dataservice.crypt;

import android.util.Base64;

import org.cryptonode.jncryptor.AES256JNCryptor;
import org.cryptonode.jncryptor.CryptorException;
import org.cryptonode.jncryptor.JNCryptor;

/**
 * Created by chen on 15/4/28.
 */
public class CryptorFactory {

    public static final String TYPE_ENCRYPT_NONE = "CLB_NONE";
    public static final String TYPE_ENCRYPT_AES = "CLB_AES";
    public static final String TYPE_ENCRYPT_QC = "CLB_QC";

    private static final String DEFAULT_PASSWORD = "ELQmHaX5ECDEJDd5r19eAWdZBzIwci4u";

    public static byte[] decryptData(String type, byte[] ciphertext) {
        return decryptData(type, ciphertext, DEFAULT_PASSWORD);
    }

    public static byte[] decryptData(String type, byte[] ciphertext, String password) {
        if (TYPE_ENCRYPT_AES.equals(type)) {
            return decryptDataWithAES(ciphertext, password);
        } else if (TYPE_ENCRYPT_QC.equals(type)) {
            return decryptDataWithQC(ciphertext, password);
        } else if (TYPE_ENCRYPT_NONE.equals(type)) {
            return ciphertext;
        } else {
            return decryptDataWithQC(ciphertext, password);
        }
    }

    private static byte[] decryptDataWithAES(byte[] ciphertext, String password) {
        byte [] decryptData = null;
        if (ciphertext == null || ciphertext.length == 0) {
            return null;
        }
        decryptData = Base64.decode(ciphertext, Base64.DEFAULT);
        JNCryptor cryptor = new AES256JNCryptor();
        try {
            return cryptor.decryptData(decryptData, password.toCharArray());
        } catch (CryptorException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] decryptDataWithQC(byte[] ciphertext, String password) {
        byte[] decryptData = null;
        if (ciphertext == null || ciphertext.length == 0) {
            return null;
        }
        try {
            decryptData = Base64.decode(ciphertext, Base64.DEFAULT);
            JNCryptor cryptor = new Cryptor();
            return cryptor.decryptData(decryptData, password.toCharArray());
        } catch (CryptorException e) {
            e.printStackTrace();
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] encryptData(String type, byte[] plaintext) {
        return encryptData(type, plaintext, DEFAULT_PASSWORD);
    }

    public static byte[] encryptData(String type, byte[] plaintext, String password) {
        if (TYPE_ENCRYPT_AES.equals(type)) {
            return encryptDataWithAES(plaintext, password);
        } else if (TYPE_ENCRYPT_QC.equals(type)) {
            return encryptDataWithQC(plaintext, password);
        } else if (TYPE_ENCRYPT_NONE.equals(type)) {
            return plaintext;
        } else {
            return encryptDataWithQC(plaintext, password);
        }
    }

    private static byte[] encryptDataWithAES(byte[] plaintext, String password) {
        if(plaintext == null || plaintext.length == 0) {
            return null;
        }

        try {
            JNCryptor cryptor = new AES256JNCryptor();
            byte[] encryptData = cryptor.encryptData(plaintext, password.toCharArray());
            return Base64.encode(encryptData, Base64.DEFAULT);
        } catch (CryptorException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static byte[] encryptDataWithQC(byte[] plaintext, String password) {
        if(plaintext == null || plaintext.length == 0) {
            return null;
        }

        try {
            JNCryptor cryptor = new Cryptor();
            byte[] encryptData = cryptor.encryptData(plaintext, password.toCharArray());
            return Base64.encode(encryptData, Base64.DEFAULT);
        } catch (CryptorException e) {
            e.printStackTrace();
        }

        return null;
    }
}
