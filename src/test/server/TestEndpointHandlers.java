package server;

import database.DatabaseWrapper;
import database.DatabaseWriteException;
import data.User;
import org.junit.Test;
import packet.response.ObjectPacket;
import server.handlers.GetUserFromIdHandler;

import static org.junit.Assert.*;

public class TestEndpointHandlers {

    @Test
    public void testBuyListingHandler() {

    }

    @Test
    public void testCreateListingHandler() {

    }

    @Test
    public void testCreateMessageHandler() {

    }

    @Test
    public void testCreateUserHandler() {

    }

    @Test
    public void testDeleteListingHandler() {

    }

    @Test
    public void testDeleteUserHandler() {

    }

    @Test
    public void testGetListingsFromAttributeHandler() {

    }

    @Test
    public void testGetMessagesBetweenUsersHandler() {

    }

    @Test
    public void testGetUserFromIdHandler() {
        DatabaseWrapper db = DatabaseWrapper.get();
        User u = new User("karma", "verysecretpassword");
        try {
            DatabaseWrapper.get().save(u);
            GetUserFromIdHandler h = new GetUserFromIdHandler();
            ObjectPacket<User> userP = (ObjectPacket) h.handle(null, new String[] {u.getId() + ""});
            assertEquals(u.getUsername(), userP.getObj().getUsername());
            assertEquals(u.getPassword(), userP.getObj().getPassword());
        } catch (DatabaseWriteException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetUsersFromAttributeHandler() {

    }
}
