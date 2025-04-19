package server;

import packet.Packet;
import packet.response.ErrorPacket;

import static org.junit.Assert.*;

/**
 * TestUtility Class
 * <p>
 * A class to help test packets for the server.
 *
 * @author Ayden Cline, lab L24
 * @version 4/12/25
 */
public class TestUtility {
    public static void assertNotErrorPacket(Packet packet) {
        if (packet instanceof ErrorPacket) {
            fail(((ErrorPacket) packet).getMessage());
        }
    }

    public static void assertErrorPacket(Packet packet) {
        if (!(packet instanceof ErrorPacket)) {
            fail("Expected ErrorPacket");
        }
    }
}
