package server.handlers;

import data.User;
import database.DatabaseWriteException;
import database.RowNotFoundException;
import org.junit.Test;
import packet.Packet;
import packet.response.ObjectPacket;

import static org.junit.Assert.assertEquals;

public class TestCreateUserHandler {
    @Test
    public void testHandle() throws DatabaseWriteException, RowNotFoundException {
        CreateUserHandler createUserHandler = new CreateUserHandler();

        // invalid headers
        Packet packet = new Packet();
        Packet resp = createUserHandler.handle(packet, new String[0]);
        TestUtility.assertErrorPacket(resp);

        // invalid password
        packet = new Packet();
        packet.addHeader("username", "my_username");
        packet.addHeader("password", "");
        resp = createUserHandler.handle(packet, new String[0]);
        TestUtility.assertErrorPacket(resp);

        // invalid username
        packet = new Packet();
        packet.addHeader("username", "");
        packet.addHeader("password", "my_password");
        resp = createUserHandler.handle(packet, new String[0]);
        TestUtility.assertErrorPacket(resp);

        // duplicate username
        new User("username", "password").save();
        packet = new Packet();
        packet.addHeader("username", "username");
        packet.addHeader("password", "my_password");
        resp = createUserHandler.handle(packet, new String[0]);
        TestUtility.assertErrorPacket(resp);

        // valid packet
        packet = new Packet();
        // ensure unique username
        String username = HandlerUtil.generateToken();
        packet.addHeader("username", username);
        packet.addHeader("password", "password");
        resp = createUserHandler.handle(packet, new String[0]);
        TestUtility.assertNotErrorPacket(resp);

        // check returned user
        User respUser = ((ObjectPacket<User>) resp).getObj();
        assertEquals(username, respUser.getUsername());

        // check that password was hashed
        User dbUser = User.getById(respUser.getId());
        assertEquals(HandlerUtil.hashPassword("password"), dbUser.getPassword());
    }
}
