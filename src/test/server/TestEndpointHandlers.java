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
import server.handlers.CreateListingHandler;
import server.handlers.GetUserFromIdHandler;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class TestEndpointHandlers {

    private void clearDb() {
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
        clearDb();
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

            Listing l3 = new Listing(s.getId(), s.getUsername(), "a keyboard",
                    "idk", 500.00, "null", false);
            DatabaseWrapper.get().save(l3);

            phl.clear();
            phl.add(new PacketHeader("buyingId", "" + u.getId()));
            phl.add( new PacketHeader("listingId", "" + l3.getId()));
            ErrorPacket e2 = (ErrorPacket) bl.handle(new Packet("this dosent matter", phl), null);
            assertEquals("User does not have enough balance to buy this item!", e2.getMessage());

            phl.clear();
            phl.add(new PacketHeader("buyingId", "adwawd"));
            phl.add( new PacketHeader("listingId", "awdawd"));
            ErrorPacket e3 = (ErrorPacket) bl.handle(new Packet("this dosent matter", phl), null);
            assertEquals("Invalid ids!", e3.getMessage());

            phl.clear();
            phl.add(new PacketHeader("buyingId", "-10"));
            phl.add( new PacketHeader("listingId", "-29"));
            ErrorPacket e4 = (ErrorPacket) bl.handle(new Packet("this dosent matter", phl), null);
            assertEquals("Could not find user or listing!", e4.getMessage());

        } catch (DatabaseWriteException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateListingHandler() {
        clearDb();

        User u = new User("karma", "1234");
        try {
            DatabaseWrapper.get().save(u);
        } catch (DatabaseWriteException e) {
            throw new RuntimeException(e);
        }
        ArrayList<PacketHeader> phl = new ArrayList<>();
        phl.add(new PacketHeader("userId", "" + u.getId()));
        phl.add( new PacketHeader("title", "pokemon cards"));
        phl.add( new PacketHeader("description", "New and rare pokemon cards"));
        phl.add( new PacketHeader("price", "10"));
        phl.add( new PacketHeader("image", "null"));
        CreateListingHandler cl = new CreateListingHandler();
        ObjectPacket<Listing> e4 = (ObjectPacket<Listing>) cl.handle(new Packet("this dosent matter", phl), null);
        Listing l = e4.getObj();
        try {
            Listing l2 = DatabaseWrapper.get().getById(Listing.class, l.getId());
            assertEquals(l.getId(), l2.getId());
            assertEquals(l.getSellerId(), l2.getSellerId());
            assertEquals(l.getTitle(), l2.getTitle());
            assertEquals(l.getDescription(), l2.getDescription());
            assertEquals(l.getPrice(), l2.getPrice(), 0.01);
            assertEquals("pokemon cards", l2.getTitle());
            assertEquals("New and rare pokemon cards", l2.getDescription());
            assertEquals(10, l2.getPrice(), 0.01);
        } catch (RowNotFoundException e) {
            throw new RuntimeException(e);
        }

        phl.clear();
        phl.add(new PacketHeader("userId", "" + u.getId()));
        phl.add( new PacketHeader("title", "pokemon cards"));
        phl.add( new PacketHeader("description", "New and rare pokemon cards"));
        phl.add( new PacketHeader("price", "-10"));
        phl.add( new PacketHeader("image", "null"));
        ErrorPacket e = (ErrorPacket) cl.handle(new Packet("this dosent matter", phl), null);
        assertEquals("Invalid listing price!", e.getMessage());
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
