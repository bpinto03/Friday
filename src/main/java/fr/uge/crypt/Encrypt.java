package fr.uge.crypt;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class Encrypt {
    private final SecretKeySpec secretKey;

    public Encrypt(String key) throws NoSuchAlgorithmException {
        this.secretKey = new SecretKeySpec(generateKey(key), "AES"); //generate secret key from key string
    }

    private byte[] generateKey(String key) throws NoSuchAlgorithmException {
        var byteKey = key.getBytes(StandardCharsets.UTF_8); //Convert key string to Array of bytes
        var sha = MessageDigest.getInstance("SHA-1");
        byteKey = sha.digest(byteKey);
        return Arrays.copyOf(byteKey, 16); // Cut the arrays to fit AES format
    }

    public String encrypt(String textToEncrypt) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(textToEncrypt.getBytes(StandardCharsets.UTF_8)));
    }

    public String decrypt(String textToDecrypt) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return new String(cipher.doFinal(Base64.getDecoder().decode(textToDecrypt)));
    }
}
