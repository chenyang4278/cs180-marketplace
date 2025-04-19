package server;

import org.junit.Assert;
import org.junit.Test;
import server.handlers.HandlerUtil;

import static org.junit.Assert.*;

/**
 * TestHandlerUtil Class
 * <p>
 * A class to test the HandlerUtil class.
 *
 * @author Ayden Cline, lab L24
 * @version 4/12/25
 */
public class TestHandlerUtil {
    @Test
    public void testHex() {
        byte[] bytes = new byte[]{(byte) 0xFF, 0x0A, 0x3B, 0x28, (byte) 0xA3, 0x00, 0x45, (byte) 0xB2};
        Assert.assertEquals("ff0a3b28a30045b2", HandlerUtil.hex(bytes));
    }

    @Test
    public void testGenerateToken() {
        String token = HandlerUtil.generateToken();
        assertEquals(64, token.length());
    }

    @Test
    public void testHashPassword() {
        String hashedPassword = HandlerUtil.hashPassword("password");
        assertEquals(
                "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8",
                hashedPassword
        );
    }
}
