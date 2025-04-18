package server;
import data.Session;
import data.User;
import database.DatabaseWrapper;
import database.DatabaseWriteException;
import org.junit.Test;
import packet.Packet;
import server.handlers.HandlerUtil;

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

    @Test
    public void testAuthenticate() throws DatabaseWriteException {
        TestingHandler t = new TestingHandler("/testing");
        Packet packet = new Packet();
        packet.addHeader("Session-Token", "invalid token");
        assertNull(t.authenticate(packet));

        User newUser = new User("username", "password");
        DatabaseWrapper.get().save(newUser);

        Session session = new Session(newUser.getId(), HandlerUtil.generateToken());
        DatabaseWrapper.get().save(session);

        packet = new Packet();
        packet.addHeader("Session-Token", session.getToken());
        assertEquals(newUser.getId(), t.authenticate(packet).getId());
    }
}
