package server.handlers;

import data.Session;
import data.User;
import database.DatabaseWrapper;
import database.DatabaseWriteException;
import org.junit.Test;
import packet.Packet;
import packet.PacketHeader;
import packet.response.ObjectPacket;

import java.util.List;

import static org.junit.Assert.*;

public class TestLoginHandler {
    @Test
    public void testHandle() throws DatabaseWriteException {
        LoginHandler loginHandler = new LoginHandler();

        // invalid packet (no headers)
        Packet packet = new Packet();
        Packet resp = loginHandler.handle(packet, new String[0]);
        TestUtility.assertErrorPacket(resp);

        // invalid header values
        packet = new Packet();
        packet.addHeader("username", "invalid username");
        packet.addHeader("password", "invalid password");
        resp = loginHandler.handle(packet, new String[0]);
        TestUtility.assertErrorPacket(resp);

        // ensure username is unique for tests
        User user = new User(HandlerUtil.generateToken(), HandlerUtil.hashPassword("my_password"));
        user.save();

        // invalid password for user
        packet = new Packet();
        packet.addHeader("username", user.getUsername());
        packet.addHeader("password", "invalid password");
        resp = loginHandler.handle(packet, new String[0]);
        TestUtility.assertErrorPacket(resp);

        // valid login
        packet = new Packet();
        packet.addHeader("username", user.getUsername());
        packet.addHeader("password", "my_password");
        resp = loginHandler.handle(packet, new String[0]);
        TestUtility.assertNotErrorPacket(resp);

        // check returned user
        User respUser = ((ObjectPacket<User>) resp).getObj();
        assertEquals(user.getId(), respUser.getId());
        assertEquals(user.getUsername(), respUser.getUsername());

        // check returned session token
        PacketHeader header = resp.getHeader("Session-Token");
        assertNotNull(header);

        String token = header.getValues().get(0);

        List<Session> sessions = DatabaseWrapper.get().filterByColumn(Session.class, "token", token);
        assertEquals(1, sessions.size());

        Session session = sessions.get(0);
        assertEquals(session.getUserId(), user.getId());
    }
}
