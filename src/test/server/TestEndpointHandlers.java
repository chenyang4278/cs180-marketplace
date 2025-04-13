package server;

import database.DatabaseWrapper;
import database.DatabaseWriteException;
import database.User;
import org.junit.Test;
import packet.response.ObjectPacket;
import server.handlers.GetUserHandler;

import static org.junit.Assert.*;

public class TestEndpointHandlers {

    @Test
    public void testGetUserHandler() {
        DatabaseWrapper db = DatabaseWrapper.get();
        User u = new User("karma", "verysecretpassword");
        try {
            u.save();
            GetUserHandler h = new GetUserHandler();
            ObjectPacket<User> userP = (ObjectPacket) h.handle(null, new String[] {u.getId() + ""});
            assertEquals(u.getUsername(), userP.getObj().getUsername());
            assertEquals(u.getPassword(), userP.getObj().getPassword());
        } catch (DatabaseWriteException e) {
            e.printStackTrace();
        }
    }

}
