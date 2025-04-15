package server.handlers;

import packet.Packet;
import packet.response.ErrorPacket;

import static org.junit.Assert.*;

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
