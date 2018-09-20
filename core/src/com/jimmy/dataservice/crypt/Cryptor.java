package com.jimmy.dataservice.crypt;

import org.cryptonode.jncryptor.AES256JNCryptor;
import org.cryptonode.jncryptor.CryptorException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by chen on 15/4/28.
 */
public class Cryptor extends AES256JNCryptor {

    @Override
    public SecretKey keyForPassword(char[] password, byte[] salt) throws CryptorException {
        byte[] secretKey = null;
        try {
            MessageDigest sha1Digest = MessageDigest.getInstance("SHA-256");
            secretKey = sha1Digest.digest(new String(password).getBytes());
            return new SecretKeySpec(secretKey, "AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
