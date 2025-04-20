package server.handlers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * HandlerUtil
 * <p>
 * Contains various operations for hashing useful in other handlers,
 * such as for login.
 *
 * @author Ayden Cline
 * @version 4/14/25
 */
public class HandlerUtil implements IHandlerUtil {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static String hex(byte[] data) {
        StringBuilder hexString = new StringBuilder();

        for (byte b : data) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }

    public static String generateToken(int size) {
        byte[] tokenBytes = new byte[size];
        SECURE_RANDOM.nextBytes(tokenBytes);
        return hex(tokenBytes);
    }

    public static String generateToken() {
        return generateToken(32);
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
