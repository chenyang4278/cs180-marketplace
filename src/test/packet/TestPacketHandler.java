package packet;
import database.RowNotFoundException;
import database.User;
import org.junit.Test;
import packet.response.ErrorPacket;
import packet.response.ObjectPacket;

import static org.junit.Assert.*;

/**
 * TestPacketHandler Class
 * <p>
 * A class to test JUnit tests for packet handlers.
 *
 * @author Karma Luitel, lab L24
 * @version 4/12/25
 */
public class TestPacketHandler {
    /**
     * TestingHandler Class
     * <p>
     * A class that extends abstract packethandler - like a test endpoint.
     *
     * @author Karma Luitel, lab L24
     * @version 4/12/25
     */
    class TestingHandler extends PacketHandler {
        public TestingHandler() {
            super("/testing/:arg1/:arg2");
        }
        @Override
        public Packet handle(Packet packet, String[] args) { return null; }
    }

    @Test
    public void testPacketMatch() {
        TestingHandler t = new TestingHandler();
        assertEquals("1", t.match("/testing/1/2")[0]);
        assertEquals("2", t.match("/testing/1/2")[1]);
    }
}
