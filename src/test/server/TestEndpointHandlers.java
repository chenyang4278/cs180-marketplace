package server;

import data.Listing;
import database.DatabaseWrapper;
import database.DatabaseWriteException;
import data.User;
import database.RowNotFoundException;
import org.junit.BeforeClass;
import org.junit.Test;
import packet.Packet;
import packet.PacketHeader;
import packet.response.ErrorPacket;
import packet.response.ObjectPacket;
import server.handlers.BuyListingHandler;
import server.handlers.GetUserFromIdHandler;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class TestEndpointHandlers {

    @BeforeClass
    public static void setUpClass() {
        // start on a clean slate
        File f = new File("id.csv");
        f.delete();
        File f0 = new File("User.csv");
        f0.delete();
        File f1 = new File("Listing.csv");
        f1.delete();
        File f2 = new File("Message.csv");
        f2.delete();
    }

    @Test
    public void testBuyListingHandler() {
        try {
            User u = new User("karma", "pass");
            User s = new User("ayden", "pass");
            s.setBalance(20);
            u.setBalance(100);
            DatabaseWrapper.get().save(u);
            DatabaseWrapper.get().save(s);
            Listing l = new Listing(s.getId(), s.getUsername(), "a keyboard",
                    "idk", 50.00, "null",
                    false);
            Listing l2 = new Listing(s.getId(), s.getUsername(), "a sold keyboard",
                    "idk", 50.00, "null",
                    true);
            DatabaseWrapper.get().save(l);
            DatabaseWrapper.get().save(l2);
            BuyListingHandler bl = new BuyListingHandler();
            ArrayList<PacketHeader> phl = new ArrayList<>();
            phl.add(new PacketHeader("buyingId", "" + u.getId()));
            phl.add( new PacketHeader("listingId", "" + l.getId()));
            ObjectPacket<User> op = (ObjectPacket<User>) bl.handle(new Packet("this dosent matter", phl), null);

            assertEquals(op.getObj().getId(), u.getId());
            assertTrue(op.getObj().getBalance() < 99);
            try {
                User s2 = DatabaseWrapper.get().getById(User.class, s.getId());
                assertTrue(DatabaseWrapper.get().getById(Listing.class, l.getId()).isSold());
                assertTrue(s2.getBalance() > 21);
            } catch (RowNotFoundException e) {
                e.printStackTrace();
            }
            phl.clear();
            phl.add(new PacketHeader("buyingId", "" + u.getId()));
            phl.add( new PacketHeader("listingId", "" + l2.getId()));
            ErrorPacket e = (ErrorPacket) bl.handle(new Packet("this dosent matter", phl), null);
            assertEquals("Item already has already been sold!", e.getMessage());
        } catch (DatabaseWriteException e) {
            e.printStackTrace();
        }
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
