package packet;
import org.junit.Test;

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
        public TestingHandler(String p) { super(p); }
        @Override
        public Packet handle(Packet packet, String[] args) { return null; }
    }

    @Test
    public void testPacketMatch() {
        TestingHandler t = new TestingHandler("/testing/:arg1/:arg2");
        assertEquals("1", t.match("/testing/1/2")[0]);
        assertEquals("2", t.match("/testing/1/2")[1]);
        TestingHandler t2 = new TestingHandler("/testing/:arg1/:arg2/:arg3");
        assertNull(t2.match("/testing/1/2"));
    }
}
