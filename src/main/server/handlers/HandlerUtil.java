package server.handlers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class HandlerUtil implements IHandlerUtil {
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String hex(byte[] data) {
        StringBuilder hexString = new StringBuilder();

        for (byte b : data) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }

    public static String generateToken() {
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        return hex(tokenBytes);
    }

    public static String hashPassword(String password) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) { // should not occur
            throw new RuntimeException(e);
        }

        return HandlerUtil.hex(digest.digest(password.getBytes()));
    }
}
