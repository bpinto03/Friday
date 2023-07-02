package fr.uge.crypt;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

public class EncryptTest {
    @Test
    @Tag("crypt")
    public void cryptWithSameKeyReturnSameCryptedPassword() throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        var e1 = new Encrypt("test");
        var e2 = new Encrypt("test");
        assertEquals(e1.encrypt("password"), e2.encrypt("password"));
    }

    @Test
    @Tag("crypt")
    public void cryptWithSameKeyReturnDifferentCryptedPassword() throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        var e1 = new Encrypt("test");
        var e2 = new Encrypt("differenttest");
        assertNotEquals(e1.encrypt("password"), e2.encrypt("password"));
    }

    @Test
    @Tag("decrypt")
    public void cryptWithSameKeyReturnSamePasswordWhileUncrypt() throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        var e1 = new Encrypt("test");
        var e2 = new Encrypt("test");
        var cryptedPassword = e1.encrypt("password");
        assertEquals(e1.decrypt(cryptedPassword), e2.decrypt(cryptedPassword));
    }

    @Test
    @Tag("decrypt")
    public void decryptCryptedPasswordWithDifferentKeyAndDiferrentEncryptionObjectReturnBadPadding() throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        var e1 = new Encrypt("test");
        var e2 = new Encrypt("differenttest");
        var cryptedPassword = e1.encrypt("password");
        assertThrows(BadPaddingException.class, () -> e2.decrypt(cryptedPassword));
    }

    @Test
    @Tag("decrypt")
    public void decrypCryptedPasswordWithDifferentKeyReturnPassword() throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        var e1 = new Encrypt("test");
        var e2 = new Encrypt("differenttest");
        var cryptedPassword1 = e1.encrypt("password");
        var cryptedPassword2 = e2.encrypt("password");
        assertEquals(e1.decrypt(cryptedPassword1), e2.decrypt(cryptedPassword2));
    }

    @Test
    public void cryptionAndEcnryptionWorks() throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        var e1 = new Encrypt("test");
        var cryptedPassword = e1.encrypt("Password");
        assertEquals("Password", e1.decrypt(cryptedPassword));
    }
}
