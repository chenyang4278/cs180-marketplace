package server;

import data.Listing;
import data.Message;
import data.Session;
import database.DatabaseWrapper;
import database.DatabaseWriteException;
import data.User;
import database.RowNotFoundException;
import org.junit.Test;
import packet.Packet;
import packet.PacketHeader;
import packet.response.ErrorPacket;
import packet.response.ObjectListPacket;
import packet.response.ObjectPacket;
import packet.response.SuccessPacket;
import server.handlers.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * TestEndpointHandlers Class
 * <p>
 * A class to test JUnit tests for endpoints.
 *
 * @author Karma Luitel, lab L24
 * @version 4/13/25
 */
public class TestEndpointHandlers {

    private void clearDb() {
        // start on a clean slate
        for (String file : new String[] { "id.csv", "User.csv", "Listing.csv", "Message.csv", "Session.csv" }) {
            new File(file).delete();
        }
    }

    private Session login() {
        User user = new User("testusername", HandlerUtil.hashPassword("my_password"));
        try {
            DatabaseWrapper.get().save(user);
            Session session = new Session(user.getId(), HandlerUtil.generateToken());
            DatabaseWrapper.get().save(session);
            return session;
        } catch (DatabaseWriteException e) {
            e.printStackTrace(); // should never happen
        }
        return null;
    }

    @Test
    public void testBuyListingHandler() {
        clearDb();
        Session session = login();
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
            phl.add( new PacketHeader("Session-Token", session.getToken()));
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
            phl.add( new PacketHeader("Session-Token", session.getToken()));
            ErrorPacket e = (ErrorPacket) bl.handle(new Packet("this dosent matter", phl), null);
            assertEquals("Item already has already been sold!", e.getMessage());

            Listing l3 = new Listing(s.getId(), s.getUsername(), "a keyboard",
                    "idk", 500.00, "null", false);
            DatabaseWrapper.get().save(l3);

            phl.clear();
            phl.add(new PacketHeader("buyingId", "" + u.getId()));
            phl.add( new PacketHeader("listingId", "" + l3.getId()));
            phl.add( new PacketHeader("Session-Token", session.getToken()));
            ErrorPacket e2 = (ErrorPacket) bl.handle(new Packet("this dosent matter", phl), null);
            assertEquals("User does not have enough balance to buy this item!", e2.getMessage());

            phl.clear();
            phl.add(new PacketHeader("buyingId", "adwawd"));
            phl.add( new PacketHeader("listingId", "awdawd"));
            phl.add( new PacketHeader("Session-Token", session.getToken()));
            ErrorPacket e3 = (ErrorPacket) bl.handle(new Packet("this dosent matter", phl), null);
            assertEquals("Invalid ids!", e3.getMessage()); // same code for all invalid ids

            phl.clear();
            phl.add(new PacketHeader("buyingId", "-10"));
            phl.add( new PacketHeader("listingId", "-29"));
            phl.add( new PacketHeader("Session-Token", session.getToken()));
            ErrorPacket e4 = (ErrorPacket) bl.handle(new Packet("this dosent matter", phl), null);
            assertEquals("Could not find user or listing!", e4.getMessage());

        } catch (DatabaseWriteException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateListingHandler() throws DatabaseWriteException, RowNotFoundException {
        clearDb();
        Session session = login();
        CreateListingHandler createListingHandler = new CreateListingHandler();

        ArrayList<PacketHeader> phl = new ArrayList<>();
        phl.add( new PacketHeader("title", "pokemon cards"));
        phl.add( new PacketHeader("description", "New and rare pokemon cards"));
        phl.add( new PacketHeader("price", "10"));
        phl.add( new PacketHeader("image", "null"));
        phl.add( new PacketHeader("Session-Token", session.getToken()));

        ObjectPacket<Listing> e4 = (ObjectPacket<Listing>) createListingHandler.handle(new Packet("", phl), null);
        Listing l = e4.getObj();
        Listing l2 = DatabaseWrapper.get().getById(Listing.class, l.getId());
        assertEquals(l.getId(), l2.getId());
        assertEquals(l.getSellerId(), l2.getSellerId());
        assertEquals(l.getTitle(), l2.getTitle());
        assertEquals(l.getDescription(), l2.getDescription());
        assertEquals(l.getPrice(), l2.getPrice(), 0.01);
        assertEquals("pokemon cards", l2.getTitle());
        assertEquals("New and rare pokemon cards", l2.getDescription());
        assertEquals(10, l2.getPrice(), 0.01);

        phl.clear();
        phl.add( new PacketHeader("title", "pokemon cards"));
        phl.add( new PacketHeader("description", "New and rare pokemon cards"));
        phl.add( new PacketHeader("price", "-10"));
        phl.add( new PacketHeader("image", "null"));
        phl.add( new PacketHeader("Session-Token", session.getToken()));

        ErrorPacket e = (ErrorPacket) createListingHandler.handle(new Packet("", phl), null);
        assertEquals("Invalid listing price!", e.getMessage());

        phl.clear();
        phl.add( new PacketHeader("title",  ""));
        phl.add( new PacketHeader("description", ""));
        phl.add( new PacketHeader("price", "10"));
        phl.add( new PacketHeader("image", "null"));
        phl.add( new PacketHeader("Session-Token", session.getToken()));
        ErrorPacket e2 = (ErrorPacket) createListingHandler.handle(new Packet("", phl), null);
        assertEquals("Invalid data", e2.getMessage());
    }

    @Test
    public void testCreateMessageHandler() {
        clearDb();
        Session session = login();

        User u1 = new User("karma", "1234");
        User u2 = new User("ayden", "123467");
        try {
            DatabaseWrapper.get().save(u1);
            DatabaseWrapper.get().save(u2);
        } catch (DatabaseWriteException e) {
            throw new RuntimeException(e);
        }

        ArrayList<PacketHeader> phl = new ArrayList<>();
        phl.add(new PacketHeader("senderId", "" + u1.getId()));
        phl.add( new PacketHeader("receiverId", "" + u2.getId()));
        phl.add( new PacketHeader("message", "hi, this is a message"));
        phl.add( new PacketHeader("Session-Token", session.getToken()));

        CreateMessageHandler cl = new CreateMessageHandler();
        ObjectPacket<Message> ol = (ObjectPacket<Message>) cl.handle(new Packet("this dosent matter", phl), null);
        Message m = ol.getObj();
        try {
            Message m2 = DatabaseWrapper.get().getById(Message.class, m.getId());
            assertEquals(m.getSenderId(), m2.getSenderId());
            assertEquals(m.getReceiverId(), m2.getReceiverId());
            assertEquals(m.getMessage(), m2.getMessage());
            assertEquals("hi, this is a message", m2.getMessage());
        } catch (RowNotFoundException e) {
            throw new RuntimeException(e);
        }

        phl.clear();
        phl.add(new PacketHeader("senderId", "" + u1.getId()));
        phl.add( new PacketHeader("receiverId", "" + u1.getId()));
        phl.add( new PacketHeader("message", "hi, this is a message"));
        phl.add( new PacketHeader("Session-Token", session.getToken()));

        ErrorPacket e = (ErrorPacket) cl.handle(new Packet("this dosent matter", phl), null);
        assertEquals("You cannot message yourself!", e.getMessage());

        phl.clear();
        phl.add(new PacketHeader("senderId", "" + u1.getId()));
        phl.add( new PacketHeader("receiverId", "" + u2.getId()));
        phl.add( new PacketHeader("message", ""));
        phl.add( new PacketHeader("Session-Token", session.getToken()));

        ErrorPacket e2 = (ErrorPacket) cl.handle(new Packet("this dosent matter", phl), null);
        assertEquals("Invalid data", e2.getMessage());
    }

    @Test
    public void testCreateUserHandler() throws DatabaseWriteException, RowNotFoundException {
        clearDb();

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

        //empty values
        new User("username", "password").save();
        packet = new Packet();
        packet.addHeader("username", "");
        packet.addHeader("password", "");
        resp = createUserHandler.handle(packet, new String[0]);
        TestUtility.assertErrorPacket(resp);

        // valid packet
        packet = new Packet();
        packet.addHeader("username", "my_username");
        packet.addHeader("password", "password");
        resp = createUserHandler.handle(packet, new String[0]);
        TestUtility.assertNotErrorPacket(resp);

        // check returned user
        User respUser = ((ObjectPacket<User>) resp).getObj();
        assertEquals("my_username", respUser.getUsername());

        // check that password was hashed
        User dbUser = DatabaseWrapper.get().getById(User.class, respUser.getId());
        assertEquals(HandlerUtil.hashPassword("password"), dbUser.getPassword());
    }

    @Test
    public void testDeleteListingHandler() {
        clearDb();
        Session session = login();

        Listing l = new Listing(10, "karma", "a keyboard",
                "idk", 50.00, "null",
                false);
        try {
            DatabaseWrapper.get().save(l );
            ArrayList<PacketHeader> phl = new ArrayList<>();
            phl.add(new PacketHeader("listingId", "" + l.getId()));
            phl.add( new PacketHeader("Session-Token", session.getToken()));

            DeleteListingHandler dl = new DeleteListingHandler();
            SuccessPacket ol = (SuccessPacket) dl.handle(new Packet("this dosent matter", phl), null);
            try {
                DatabaseWrapper.get().getById(Listing.class, l.getId());
                assertTrue(false);
            } catch (RowNotFoundException e) {
                assertTrue(true);
            }

            phl.clear();
            phl.add(new PacketHeader("listingId", "-10"));
            phl.add( new PacketHeader("Session-Token", session.getToken()));

            ErrorPacket e = (ErrorPacket) dl.handle(new Packet("this dosent matter", phl), null);
            assertEquals("Listing does not exist!", e.getMessage());
        } catch (DatabaseWriteException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testDeleteUserHandler() {
        clearDb();
        Session session = login();

        User u1 = new User("karma", "1234");
        try {
            DatabaseWrapper.get().save(u1);
            ArrayList<PacketHeader> phl = new ArrayList<>();
            phl.add(new PacketHeader("userId", "" + u1.getId()));
            phl.add( new PacketHeader("Session-Token", session.getToken()));

            DeleteUserHandler dl = new DeleteUserHandler();
            SuccessPacket ol = (SuccessPacket) dl.handle(new Packet("this dosent matter", phl), null);
            try {
                DatabaseWrapper.get().getById(User.class, u1.getId());
                assertTrue(false);
            } catch (RowNotFoundException e) {
                assertTrue(true);
            }

            phl.clear();
            phl.add(new PacketHeader("userId", "-10"));
            phl.add( new PacketHeader("Session-Token", session.getToken()));

            ErrorPacket e = (ErrorPacket) dl.handle(new Packet("this dosent matter", phl), null);
            assertEquals("User does not exist!", e.getMessage());
        } catch (DatabaseWriteException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetListingsFromAttributeHandler() {
        clearDb();
        Session session = login();

        Listing l = new Listing(0, "ayden", "a keyboard",
                "idk", 50.00, "null",
                false);
        Listing l2 = new Listing(1, "karma", "a sold keyboard",
                "idk", 20.00, "null",
                true);
        Listing l3 = new Listing(1, "karma", "a 2 keyboard",
                "idk", 20.00, "null",
                true);
        Listing l4 = new Listing(2, "chen", "a 3 keyboard",
                "idk", 50.00, "null",
                true);
        Listing l5 = new Listing(3, "idk", "a 4 keyboard",
                "idk", 50.00, "null",
                true);
        Listing l6 = new Listing(1, "karma", "a 5 keyboard",
                "idk", 50.00, "null",
                true);

        try {
            DatabaseWrapper.get().save(l);
            DatabaseWrapper.get().save(l2);
            DatabaseWrapper.get().save(l3);
            DatabaseWrapper.get().save(l4);
            DatabaseWrapper.get().save(l5);
            DatabaseWrapper.get().save(l6);
        } catch (DatabaseWriteException e) {
            throw new RuntimeException(e);
        }

        ArrayList<PacketHeader> phl = new ArrayList<>();
        phl.add(new PacketHeader("attribute", "sellerId"));
        phl.add(new PacketHeader("attributeVal", "1"));
        phl.add( new PacketHeader("Session-Token", session.getToken()));

        GetListingsFromAttributeHandler h = new GetListingsFromAttributeHandler();
        ObjectListPacket<Listing> o = (ObjectListPacket<Listing>) h.handle(new Packet("this dosent matter", phl), null);
        assertEquals(3, o.getObjList().size());

        phl.clear();
        phl.add(new PacketHeader("attribute", "sellerName"));
        phl.add(new PacketHeader("attributeVal", "idk"));
        phl.add( new PacketHeader("Session-Token", session.getToken()));

        ObjectListPacket<Listing> o2 = (ObjectListPacket<Listing>) h.handle(new Packet("this dosent matter", phl), null);
        assertEquals(1, o2.getObjList().size());
        assertEquals("a 4 keyboard", o2.getObjList().get(0).getTitle());

        phl.clear();
        phl.add(new PacketHeader("attribute", "image"));
        phl.add(new PacketHeader("attributeVal", "null"));
        phl.add( new PacketHeader("Session-Token", session.getToken()));

        ObjectListPacket<Listing> o3 = (ObjectListPacket<Listing>) h.handle(new Packet("this dosent matter", phl), null);
        assertEquals(6, o3.getObjList().size());
    }

    @Test
    public void testGetMessagesBetweenUsersHandler() {
        clearDb();
        Session session = login();

        User u1 = new User("karma", "verysecretpassword");
        User u2 = new User("ayden", "extrasecretpassword");
        User u3 = new User("chen", "extrasecretpassword");
        try {
            DatabaseWrapper.get().save(u1);
            DatabaseWrapper.get().save(u2);
            DatabaseWrapper.get().save(u3);
        } catch (DatabaseWriteException e) {
            throw new RuntimeException(e);
        }
        Message m1 = new Message(u1.getId(), u2.getId(), "this is a message from u1 to u2");
        //wait before creating next message, this may error very rarely
        for (int i = 0; i < 1000000000; i+=2) {
            i--;
        }
        Message m2 = new Message(u1.getId(), u2.getId(), "this is a another message from u1 to u2");
        Message m3 = new Message(u1.getId(), u3.getId(), "this is a message from u1 to u3");
        Message m4 = new Message(u3.getId(), u2.getId(), "this is a another message from u3 to u2");
        Message m5 = new Message(u2.getId(), u3.getId(), "this is a another message from u2 to u3");
        try {
            DatabaseWrapper.get().save(m1);
            DatabaseWrapper.get().save(m2);
            DatabaseWrapper.get().save(m3);
            DatabaseWrapper.get().save(m4);
            DatabaseWrapper.get().save(m5);
        } catch (DatabaseWriteException e) {
            throw new RuntimeException(e);
        }

        GetMessagesBetweenUsersHandler h = new GetMessagesBetweenUsersHandler();

        ArrayList<PacketHeader> phl = new ArrayList<>();
        phl.add(new PacketHeader("senderId", "" + u1.getId()));
        phl.add(new PacketHeader("receiverId", "" + u2.getId()));
        phl.add( new PacketHeader("Session-Token", session.getToken()));

        ObjectListPacket<Message> o = (ObjectListPacket<Message>) h.handle(new Packet("this dosent matter", phl), null);
        assertEquals(2, o.getObjList().size());

        //TODO Fix this?
        //dependent on wait before creating next message, this may error very, very rarely
        assertTrue(o.getObjList().get(0).getTimestamp() < o.getObjList().get(1).getTimestamp());

        phl.clear();
        phl.add(new PacketHeader("senderId", "" + u2.getId()));
        phl.add(new PacketHeader("receiverId", "" + u3.getId()));
        phl.add( new PacketHeader("Session-Token", session.getToken()));

        ObjectListPacket<Message> o2 = (ObjectListPacket<Message>) h.handle(new Packet("this dosent matter", phl), null);
        assertEquals(1, o2.getObjList().size());
        assertEquals("this is a another message from u2 to u3", o2.getObjList().get(0).getMessage());

        phl.clear();
        phl.add(new PacketHeader("senderId", "" + u3.getId()));
        phl.add(new PacketHeader("receiverId", "" + u2.getId()));
        phl.add( new PacketHeader("Session-Token", session.getToken()));

        ObjectListPacket<Message> o3 = (ObjectListPacket<Message>) h.handle(new Packet("this dosent matter", phl), null);
        assertEquals(1, o2.getObjList().size());
        assertEquals("this is a another message from u3 to u2", o3.getObjList().get(0).getMessage());

        phl.clear();
        phl.add(new PacketHeader("senderId", "" + u3.getId()));
        phl.add(new PacketHeader("receiverId", "" + u3.getId()));
        phl.add( new PacketHeader("Session-Token", session.getToken()));

        ErrorPacket o4 = (ErrorPacket) h.handle(new Packet("this dosent matter", phl), null);
        assertEquals("You cannot message yourself!", o4.getMessage());
    }

    @Test
    public void testGetUserFromIdHandler() {
        clearDb();
        Session session = login();

        User u = new User("karma", "verysecretpassword");
        try {
            DatabaseWrapper.get().save(u);
            GetUserFromIdHandler h = new GetUserFromIdHandler();
            Packet p = new Packet();
            p.addHeader("Session-Token", session.getToken());
            ObjectPacket<User> userP = (ObjectPacket) h.handle(p, new String[] {u.getId() + ""});
            assertEquals(u.getUsername(), userP.getObj().getUsername());
            assertEquals(u.getPassword(), userP.getObj().getPassword());
        } catch (DatabaseWriteException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetUsersFromAttributeHandler() {
        clearDb();
        Session session = login();

        User u1 = new User("karma", "1234");
        User u2 = new User("karma1", "1awd234");
        User u3 = new User("karma2", "1234a");
        User u4 = new User("karma3", "12312214");
        User u5 = new User("karma4", "1234");
        User u6 = new User("karma5", "1234");

        try {
            DatabaseWrapper.get().save(u1);
            DatabaseWrapper.get().save(u2);
            DatabaseWrapper.get().save(u3);
            DatabaseWrapper.get().save(u4);
            DatabaseWrapper.get().save(u5);
            DatabaseWrapper.get().save(u6);
        } catch (DatabaseWriteException e) {
            throw new RuntimeException(e);
        }

        ArrayList<PacketHeader> phl = new ArrayList<>();
        phl.add(new PacketHeader("attribute", "username"));
        phl.add(new PacketHeader("attributeVal", "karma"));
        phl.add( new PacketHeader("Session-Token", session.getToken()));

        GetUsersFromAttributeHandler h = new GetUsersFromAttributeHandler();
        ObjectListPacket<User> o = (ObjectListPacket<User>) h.handle(new Packet("this dosent matter", phl), null);
        assertEquals(1, o.getObjList().size());
        assertEquals("karma", o.getObjList().get(0).getUsername());

        phl.clear();
        phl.add(new PacketHeader("attribute", "password"));
        phl.add(new PacketHeader("attributeVal", "1awd234"));
        phl.add( new PacketHeader("Session-Token", session.getToken()));

        ObjectListPacket<User> o2 = (ObjectListPacket<User>) h.handle(new Packet("this dosent matter", phl), null);
        assertEquals(1, o2.getObjList().size());
        assertEquals("1awd234", o2.getObjList().get(0).getPassword());

        phl.clear();
        phl.add(new PacketHeader("attribute", "password"));
        phl.add(new PacketHeader("attributeVal", "1234"));
        phl.add( new PacketHeader("Session-Token", session.getToken()));

        ObjectListPacket<User> o3 = (ObjectListPacket<User>) h.handle(new Packet("this dosent matter", phl), null);
        assertEquals(3, o3.getObjList().size());

        phl.clear();
        phl.add(new PacketHeader("attribute", "password"));
        phl.add(new PacketHeader("attributeVal", "12222234"));
        phl.add( new PacketHeader("Session-Token", session.getToken()));

        ObjectListPacket<User> o4 = (ObjectListPacket<User>) h.handle(new Packet("this dosent matter", phl), null);
        assertEquals(0, o4.getObjList().size());
    }

    @Test
    public void testLoginHandler() throws DatabaseWriteException {
        clearDb();

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

        User user = new User("username", HandlerUtil.hashPassword("my_password"));
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
