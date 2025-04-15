package server.handlers;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestHandlerUtil {
    @Test
    public void testHex() {
        byte[] bytes = new byte[] {(byte) 0xFF, 0x0A, 0x3B, 0x28, (byte) 0xA3, 0x00, 0x45, (byte) 0xB2};
        assertEquals("ff0a3b28a30045b2", HandlerUtil.hex(bytes));
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
