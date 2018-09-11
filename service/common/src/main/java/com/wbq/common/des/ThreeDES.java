package com.wbq.common.des;

import org.checkerframework.checker.units.qual.K;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;

/**
 *  * 3DES加密方法
 *  * @author biqin.wu
 *  * @since 11 九月 2018
 *  
 */
public class ThreeDES {
    private static final Logger logger = LoggerFactory.getLogger(ThreeDES.class);

    private static final String KEY = "qwerasd@!#xcv1237890wer";

    private SecretKey secretKey = buildSecretKey();

    /**
     * encrypt bytes
     *
     * @param plainByte plain bytes
     * @return encrypted bytes
     */
    public byte[] encrypt(byte[] plainByte) {
        byte[] encryptBytes = null;
        Cipher cipher = buildCipher();
        Objects.requireNonNull(cipher);
        try {
            Objects.requireNonNull(secretKey);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            encryptBytes = cipher.doFinal(plainByte);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            logger.error("fail to encrypt plainByte, plainByte={}", plainByte);
        } catch (InvalidKeyException e) {
            logger.error("fail to init cipher", e);
        }
        return encryptBytes;
    }

    /**
     * decrypt bytes
     *
     * @param cipherBytes encrypt bytes
     * @return decrypted bytes
     */
    public byte[] decrypt(byte[] cipherBytes) {
        byte[] decryptBytes = null;
        Cipher cipher = buildCipher();
        Objects.requireNonNull(cipher);
        try {
            Objects.requireNonNull(secretKey);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            decryptBytes = cipher.doFinal(cipherBytes);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            logger.error("fail to decrypt decrypt, cipherBytes={}", cipherBytes);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return decryptBytes;
    }

    private SecretKey buildSecretKey() {
        try {
            DESedeKeySpec dks = new DESedeKeySpec(KEY.getBytes(StandardCharsets.UTF_8));
            SecretKeyFactory skf = SecretKeyFactory.getInstance("DESede");
            return skf.generateSecret(dks);
        } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            logger.error("fail to generate SecretKey", e);
        }
        return null;
    }

    private Cipher buildCipher() {
        try {
            return Cipher.getInstance("DESede/ECB/PKCS5Padding");
        } catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
            logger.error("fail to instance Cipher", e);
        }
        return null;
    }
}
