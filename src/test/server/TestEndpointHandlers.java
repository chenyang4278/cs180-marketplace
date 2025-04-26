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
import server.handlers.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * TestEndpointHandlers Class
 * <p>
 * A class to run JUnit tests for endpoints.
 *
 * @author Karma Luitel, lab L24
 * @version 4/13/25
 */
public class TestEndpointHandlers {

    /**
     * SessionInfo Class
     * <p>
     * A private class to handle making sessions only used in testing.
     *
     * @author Ayden Cline, lab L24
     * @version 4/13/25
     */
    private static class SessionInfo {
        private Session session;
        private User user;

        public SessionInfo(Session session, User user) {
            this.session = session;
            this.user = user;
        }

        public Packet makePacket() {
            Packet packet = new Packet("");
            packet.addHeader("Session-Token", session.getToken());
            return packet;
        }
    }

    private static SessionInfo sessionInfo;

    private void clearDb() {
        // start on a clean slate
        for (String file : new String[]{"id.csv", "User.csv", "Listing.csv", "Message.csv", "Session.csv"}) {
            new File(file).delete();
        }
        sessionInfo = null;
    }

    private void getSession() {
        if (sessionInfo == null) {
            try {
                User user = new User("testusername", HandlerUtil.hashPassword("my_password"));
                user.setBalance(80);
                DatabaseWrapper.get().save(user);
                Session session = new Session(user.getId(), HandlerUtil.generateToken());
                DatabaseWrapper.get().save(session);
                sessionInfo = new SessionInfo(session, user);
            } catch (DatabaseWriteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void testBuyListingHandler() throws DatabaseWriteException, RowNotFoundException {
        clearDb();
        getSession();
        BuyListingHandler handler = new BuyListingHandler();
        DatabaseWrapper db = DatabaseWrapper.get();

        User seller = new User("ayden", "pass");
        seller.setBalance(20);
        DatabaseWrapper.get().save(seller);

        Listing listing = new Listing(seller.getId(), seller.getUsername(), "a keyboard",
                "idk", 50.00, "null",
                false);
        DatabaseWrapper.get().save(listing);

        // test successful buy
        Packet packet = sessionInfo.makePacket();
        ObjectPacket<User> updatedUser = (ObjectPacket<User>) handler.handle(
                packet,
                new String[]{"" + listing.getId()}
        );
        // check updated user
        assertEquals(updatedUser.getObj().getId(), sessionInfo.user.getId());
        assertTrue(updatedUser.getObj().getBalance() < 99);
        // check updated seller
        User updatedSeller = db.getById(User.class, seller.getId());
        assertTrue(db.getById(Listing.class, listing.getId()).isSold());
        assertTrue(updatedSeller.getBalance() > 21);

        // test trying to buy an already sold item
        packet = sessionInfo.makePacket();
        ErrorPacket err = (ErrorPacket) handler.handle(packet, new String[]{"" + listing.getId()});
        assertEquals("Item already has already been sold!", err.getMessage());

        Listing listing2 = new Listing(seller.getId(), seller.getUsername(), "a keyboard",
                "idk", 500.00, "null", false);
        DatabaseWrapper.get().save(listing2);

        // test not enough balance
        packet = sessionInfo.makePacket();
        ErrorPacket e2 = (ErrorPacket) handler.handle(packet, new String[]{"" + listing2.getId()});
        assertEquals("User does not have enough balance to buy this item!", e2.getMessage());

        packet = sessionInfo.makePacket();
        ErrorPacket e3 = (ErrorPacket) handler.handle(packet, new String[]{"invalid id"});
        assertEquals("Invalid ids!", e3.getMessage()); // same code for all invalid ids

        packet = sessionInfo.makePacket();
        ErrorPacket e4 = (ErrorPacket) handler.handle(packet, new String[]{"-29"});
        assertEquals("Could not find user or listing!", e4.getMessage());
    }

    @Test
    public void testCreateListingHandler() throws RowNotFoundException {
        clearDb();
        getSession();
        CreateListingHandler createListingHandler = new CreateListingHandler();

        Packet packet = sessionInfo.makePacket();
        packet.addHeader("title", "pokemon cards");
        packet.addHeader("description", "New and rare pokemon cards");
        packet.addHeader("price", "10");
        packet.addHeader("image", "null");

        // successfully create listing
        ObjectPacket<Listing> e4 = (ObjectPacket<Listing>) createListingHandler.handle(packet, null);
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

        // invalid price
        packet = sessionInfo.makePacket();
        packet.addHeader("title", "pokemon cards");
        packet.addHeader("description", "New and rare pokemon cards");
        packet.addHeader("price", "-10");
        packet.addHeader("image", "null");

        ErrorPacket e = (ErrorPacket) createListingHandler.handle(packet, null);
        assertEquals("Invalid listing price!", e.getMessage());

        // invalid data
        packet = sessionInfo.makePacket();
        packet.addHeader("title", "");
        packet.addHeader("description", "");
        packet.addHeader("price", "10");
        packet.addHeader("image", "null");

        ErrorPacket e2 = (ErrorPacket) createListingHandler.handle(packet, null);
        assertEquals("Invalid data", e2.getMessage());
    }

    @Test
    public void testCreateMessageHandler() throws DatabaseWriteException {
        clearDb();
        getSession();
        CreateMessageHandler handler = new CreateMessageHandler();

        User receiver = new User("ayden", "123467");
        DatabaseWrapper.get().save(receiver);


        // successfully create message
        Packet packet = sessionInfo.makePacket();
        packet.addHeader("receiverId", "" + receiver.getId());
        packet.addHeader("message", "hi, this is a message");

        ObjectPacket<Message> resp = (ObjectPacket<Message>) handler.handle(packet, null);
        Message msg = resp.getObj();
        assertEquals(sessionInfo.user.getId(), msg.getSenderId());
        assertEquals(receiver.getId(), msg.getReceiverId());
        assertEquals("hi, this is a message", msg.getMessage());

        // error sending message to self
        packet = sessionInfo.makePacket();
        packet.addHeader("receiverId", "" + sessionInfo.user.getId());
        packet.addHeader("message", "hi, this is a message");

        ErrorPacket err = (ErrorPacket) handler.handle(packet, null);
        assertEquals("You cannot message yourself!", err.getMessage());

        // empty message error
        packet = sessionInfo.makePacket();
        packet.addHeader("receiverId", "" + receiver.getId());
        packet.addHeader("message", "");

        ErrorPacket e2 = (ErrorPacket) handler.handle(packet, null);
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
        DatabaseWrapper.get().save(new User("username", "password"));
        packet = new Packet();
        packet.addHeader("username", "username");
        packet.addHeader("password", "my_password");
        resp = createUserHandler.handle(packet, new String[0]);
        TestUtility.assertErrorPacket(resp);

        //empty values
        DatabaseWrapper.get().save(new User("username", "password"));
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
    public void testDeleteListingHandler() throws DatabaseWriteException {
        clearDb();
        getSession();
        DeleteListingHandler handler = new DeleteListingHandler();

        Listing listing = new Listing(sessionInfo.user.getId(), "karma", "a keyboard",
                "idk", 50.00, "null", false);
        DatabaseWrapper.get().save(listing);

        // successfully delete
        Packet packet = sessionInfo.makePacket();
        Packet resp = handler.handle(packet, new String[]{"" + listing.getId()});
        TestUtility.assertNotErrorPacket(resp);
        try {
            DatabaseWrapper.get().getById(Listing.class, listing.getId());
            fail("Listing was not deleted as expected");
        } catch (RowNotFoundException ignored) {
            assertTrue(true);
        }

        // invalid id
        packet = sessionInfo.makePacket();
        ErrorPacket err = (ErrorPacket) handler.handle(packet, new String[]{"" + listing.getId()});
        assertEquals("Listing does not exist!", err.getMessage());
    }

    @Test
    public void testDeleteUserHandler() throws DatabaseWriteException {
        clearDb();
        DeleteUserHandler handler = new DeleteUserHandler();

        User user = new User("karma", "1234");
        DatabaseWrapper.get().save(user);

        Session session = new Session(user.getId(), HandlerUtil.generateToken());
        DatabaseWrapper.get().save(session);

        String[] args = new String[]{"" + user.getId()};

        // successfully delete user
        Packet packet = new Packet();
        packet.addHeader("Session-Token", session.getToken());
        handler.handle(packet, args);
        try {
            DatabaseWrapper.get().getById(User.class, user.getId());
            fail("User was not deleted as expected");
        } catch (RowNotFoundException e) {
            assertTrue(true);
        }

        // try to delete user that does not exist
        ErrorPacket e = (ErrorPacket) handler.handle(packet, args);
        // will cause login to fail if user is deleted.
        assertEquals("Not logged in", e.getMessage());
    }

    @Test
    public void testEditListingHandler() throws DatabaseWriteException, RowNotFoundException {
        clearDb();
        getSession();
        EditListingHandler handler = new EditListingHandler();
        DatabaseWrapper db = DatabaseWrapper.get();

        Listing listing = new Listing(sessionInfo.user.getId(), sessionInfo.user.getUsername(), "a keyboard",
                "idk", 50.00, "null", false);
        DatabaseWrapper.get().save(listing);

        String[] args = new String[]{"" + listing.getId()};

        // successfully edit sold
        Packet packet = sessionInfo.makePacket();
        packet.addHeader("attribute", "sold");
        packet.addHeader("attributeVal", "true");
        Packet resp = handler.handle(packet, args);
        TestUtility.assertNotErrorPacket(resp);

        ObjectPacket<Listing> objResp = (ObjectPacket<Listing>) resp;
        assertTrue(objResp.getObj().isSold());
        Listing dbListing = db.getById(Listing.class, listing.getId());
        assertTrue(dbListing.isSold());

        // successfully edit price
        packet = sessionInfo.makePacket();
        packet.addHeader("attribute", "price");
        packet.addHeader("attributeVal", "20.1");
        resp = handler.handle(packet, args);
        TestUtility.assertNotErrorPacket(resp);

        dbListing = DatabaseWrapper.get().getById(Listing.class, listing.getId());
        assertEquals(20.1, dbListing.getPrice(), 0.01);

        // invalid attribute
        packet = sessionInfo.makePacket();
        packet.addHeader("attribute", "aiawd");
        packet.addHeader("attributeVal", "20.1");
        TestUtility.assertErrorPacket(handler.handle(packet, args));

        // invalid price
        packet = sessionInfo.makePacket();
        packet.addHeader("attribute", "price");
        packet.addHeader("attributeVal", "thisisnotanumber");
        Packet p3 = handler.handle(packet, args);
        TestUtility.assertErrorPacket(p3);
    }

    @Test
    public void testEditUserHandler() throws RowNotFoundException, DatabaseWriteException {
        clearDb();
        getSession();
        EditUserHandler handler = new EditUserHandler();

        String[] args = new String[]{"" + sessionInfo.user.getId()};

        // edit username
        Packet packet = sessionInfo.makePacket();
        packet.addHeader("attribute", "username");
        packet.addHeader("attributeVal", "karma12");
        ObjectPacket<User> resp = (ObjectPacket<User>) handler.handle(packet, args);

        User dbUser = DatabaseWrapper.get().getById(User.class, sessionInfo.user.getId());
        assertEquals("karma12", dbUser.getUsername());
        assertEquals("karma12", resp.getObj().getUsername());

        // edit balance
        packet = sessionInfo.makePacket();
        packet.addHeader("attribute", "balance");
        packet.addHeader("attributeVal", "1000");
        handler.handle(packet, args);

        dbUser = DatabaseWrapper.get().getById(User.class, sessionInfo.user.getId());
        assertEquals(1000, dbUser.getBalance(), 0.01);

        // invalid attribute
        packet = sessionInfo.makePacket();
        packet.addHeader("attribute", "awdawda");
        packet.addHeader("attributeVal", "1000");
        TestUtility.assertErrorPacket(handler.handle(packet, args));

        // duplicate username error
        DatabaseWrapper.get().save(new User("duplicate username", ""));
        packet = sessionInfo.makePacket();
        packet.addHeader("attribute", "username");
        packet.addHeader("attributeVal", "duplicate username");
        Packet p3 = handler.handle(packet, args);
        TestUtility.assertErrorPacket(p3);
    }

    @Test
    public void testGetListingsFromAttributeHandler() throws DatabaseWriteException {
        clearDb();
        getSession();
        GetListingsFromAttributeHandler handler = new GetListingsFromAttributeHandler();

        Listing[] listings = new Listing[]{
            new Listing(0, "ayden", "a keyboard",
                    "idk", 50.00, "null", false),
            new Listing(1, "karma", "a sold keyboard",
                    "idk", 20.00, "null", true),
            new Listing(1, "karma", "a 2 keyboard",
                    "idk", 20.00, "null", true),
            new Listing(2, "chen", "a 3 keyboard",
                    "idk", 50.00, "null", true),
            new Listing(3, "idk", "a 4 keyboard",
                    "idk", 50.00, "null", true),
            new Listing(1, "karma", "a 5 keyboard",
                    "idk", 50.00, "null", true)
        };
        for (Listing listing : listings) {
            DatabaseWrapper.get().save(listing);
        }

        Packet packet = sessionInfo.makePacket();
        packet.addHeader("attribute", "sellerId");
        packet.addHeader("attributeVal", "1");

        ObjectListPacket<Listing> o = (ObjectListPacket<Listing>) handler.handle(packet, null);
        assertEquals(3, o.getObjList().size());

        packet = sessionInfo.makePacket();
        packet.addHeader("attribute", "sellerName");
        packet.addHeader("attributeVal", "iD k");

        ObjectListPacket<Listing> o2 = (ObjectListPacket<Listing>) handler.handle(packet, null);
        assertEquals(1, o2.getObjList().size());
        assertEquals("a 4 keyboard", o2.getObjList().get(0).getTitle());

        packet = sessionInfo.makePacket();
        packet.addHeader("attribute", "image");
        packet.addHeader("attributeVal", "null");

        ObjectListPacket<Listing> o3 = (ObjectListPacket<Listing>) handler.handle(packet, null);
        assertEquals(6, o3.getObjList().size());
    }

    @Test
    public void testGetMessagesBetweenUsersHandler() throws DatabaseWriteException, InterruptedException {
        clearDb();
        getSession();
        GetMessagesBetweenUsersHandler handler = new GetMessagesBetweenUsersHandler();

        User user1 = new User("karma", "verysecretpassword");
        DatabaseWrapper.get().save(user1);
        User user2 = new User("ayden", "extrasecretpassword");
        DatabaseWrapper.get().save(user2);

        Message[] messages = new Message[5];
        // add delay so that the timestamps are spaced out
        messages[0] = new Message(user1.getId(), sessionInfo.user.getId(), "this is a message from u1 to me");
        Thread.sleep(100);
        messages[1] = new Message(sessionInfo.user.getId(), user2.getId(), "this is a message from me to u2");
        Thread.sleep(100);
        messages[2] = new Message(user2.getId(), sessionInfo.user.getId(), "this is a message from u2 to me");
        Thread.sleep(100);
        messages[3] = new Message(sessionInfo.user.getId(), user1.getId(), "this is a message from me to u1");
        Thread.sleep(100);
        messages[4] = new Message(sessionInfo.user.getId(), user2.getId(), "this is another message from me to u2");
        for (Message msg : messages) {
            DatabaseWrapper.get().save(msg);
        }

        // check messages between me and user 1
        Packet packet = sessionInfo.makePacket();
        packet.addHeader("otherUserId", "" + user1.getId());

        Packet resp = handler.handle(packet, null);
        TestUtility.assertNotErrorPacket(resp);
        ObjectListPacket<Message> user1Inbox = (ObjectListPacket<Message>) resp;
        assertEquals(2, user1Inbox.getObjList().size());
        assertTrue(user1Inbox.getObjList().get(0).getTimestamp() >= user1Inbox.getObjList().get(1).getTimestamp());
        assertEquals("this is a message from me to u1", user1Inbox.getObjList().get(0).getMessage());
        assertEquals("this is a message from u1 to me", user1Inbox.getObjList().get(1).getMessage());

        // check messages between me and user 2
        packet = sessionInfo.makePacket();
        packet.addHeader("otherUserId", "" + user2.getId());

        resp = handler.handle(packet, null);
        TestUtility.assertNotErrorPacket(resp);
        ObjectListPacket<Message> user2Inbox = (ObjectListPacket<Message>) resp;
        assertEquals(3, user2Inbox.getObjList().size());
        assertTrue(user2Inbox.getObjList().get(0).getTimestamp() >= user2Inbox.getObjList().get(1).getTimestamp());
        assertEquals("this is another message from me to u2", user2Inbox.getObjList().get(0).getMessage());
        assertEquals("this is a message from u2 to me", user2Inbox.getObjList().get(1).getMessage());
        assertEquals("this is a message from me to u2", user2Inbox.getObjList().get(2).getMessage());
    }

    @Test
    public void testGetUserFromIdHandler() throws DatabaseWriteException {
        clearDb();
        getSession();
        GetUserFromIdHandler handler = new GetUserFromIdHandler();

        // get me
        Packet packet = sessionInfo.makePacket();
        Packet resp = handler.handle(
                packet,
                new String[]{"" + sessionInfo.user.getId()}
        );
        TestUtility.assertNotErrorPacket(resp);
        ObjectPacket<User> userResp = (ObjectPacket<User>) resp;
        assertEquals(sessionInfo.user.getId(), userResp.getObj().getId());
        assertEquals(sessionInfo.user.getUsername(), userResp.getObj().getUsername());

        // get new user
        User newUser = new User("karma", "verysecretpassword");
        DatabaseWrapper.get().save(newUser);

        packet = sessionInfo.makePacket();
        resp = handler.handle(packet, new String[]{"" + newUser.getId()});
        TestUtility.assertNotErrorPacket(resp);
        userResp = (ObjectPacket<User>) resp;
        assertEquals(newUser.getId(), userResp.getObj().getId());
        assertEquals(newUser.getUsername(), userResp.getObj().getUsername());
    }

    @Test
    public void testGetUsersFromAttributeHandler() throws DatabaseWriteException {
        clearDb();
        getSession();
        GetUsersFromAttributeHandler handler = new GetUsersFromAttributeHandler();

        User[] users = new User[]{
            new User("karma", "1234"),
            new User("karma1", "1awd234"),
            new User("karma2", "1234a"),
            new User("karma3", "12312214"),
            new User("karma4", "1234"),
            new User("karma5", "1234"),
            new User("ian", "pw")
        };
        users[3].setBalance(50.0);
        users[4].setBalance(50.0);
        for (User user : users) {
            DatabaseWrapper.get().save(user);
        }

        Packet packet = sessionInfo.makePacket();
        packet.addHeader("attribute", "username");
        packet.addHeader("attributeVal", "k  Arma");
        packet.addHeader("leniency", "true");

        ObjectListPacket<User> resp = (ObjectListPacket<User>) handler.handle(packet, null);
        assertEquals(1, resp.getObjList().size());
        assertEquals("karma", resp.getObjList().get(0).getUsername());
        assertNull(resp.getObjList().get(0).getPassword());

        packet = sessionInfo.makePacket();
        packet.addHeader("attribute", "password");
        packet.addHeader("attributeVal", "1awd234");
        packet.addHeader("leniency", "true");

        Packet err = handler.handle(packet, null);
        TestUtility.assertErrorPacket(err);

        packet = sessionInfo.makePacket();
        packet.addHeader("attribute", "balance");
        packet.addHeader("attributeVal", "50.0");
        packet.addHeader("leniency", "true");

        resp = (ObjectListPacket<User>) handler.handle(packet, null);
        assertEquals(2, resp.getObjList().size());
        assertEquals(null, resp.getObjList().get(0).getPassword());
        assertEquals(50.0, resp.getObjList().get(0).getBalance(), 0.1);

        packet = sessionInfo.makePacket();
        packet.addHeader("attribute", "balance");
        packet.addHeader("attributeVal", "200.0");
        packet.addHeader("leniency", "true");

        resp = (ObjectListPacket<User>) handler.handle(packet, null);
        assertEquals(0, resp.getObjList().size());

        
        packet = sessionInfo.makePacket();
        packet.addHeader("attribute", "username");
        packet.addHeader("attributeVal", "Ian");
        packet.addHeader("leniency", "false");

        resp = (ObjectListPacket<User>) handler.handle(packet, null);
        assertEquals(0, resp.getObjList().size());
        
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
        DatabaseWrapper.get().save(user);

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

        List<Session> sessions = DatabaseWrapper.get().filterByColumn(Session.class, "token", token, false);
        assertEquals(1, sessions.size());

        Session session = sessions.get(0);
        assertEquals(session.getUserId(), user.getId());
    }

    @Test
    public void testImageUploadHandler() {
        clearDb();
        getSession();
        ImageUploadHandler imageUploadHandler = new ImageUploadHandler();

        Packet packet = sessionInfo.makePacket();

        Packet resp = imageUploadHandler.handle(packet, null);
        TestUtility.assertErrorPacket(resp);

        packet.addHeader("File-Hash", "hash");

        resp = imageUploadHandler.handle(packet, null);
        TestUtility.assertNotErrorPacket(resp);
        assertEquals("hash", resp.getHeader("File-Hash").getValues().get(0));
    }

    @Test
    public void testImageDownloadHandler() throws IOException {
        clearDb();
        ImageDownloadHandler handler = new ImageDownloadHandler();

        new File("static").mkdir();
        new File("static/hash").createNewFile();

        Packet packet = new Packet();

        Packet resp = handler.handle(packet, new String[] { "invalid hash" });
        TestUtility.assertErrorPacket(resp);

        resp = handler.handle(packet, new String[] { "hash" });
        TestUtility.assertNotErrorPacket(resp);
        assertEquals("hash", resp.getHeader("Download-Hash").getValues().get(0));
    }

    @Test
    public void testGetInboxUsersHandler() throws DatabaseWriteException {
        clearDb();
        getSession();
        GetInboxUsersHandler handler = new GetInboxUsersHandler();
        DatabaseWrapper db = DatabaseWrapper.get();

        User user1 = new User("user1", "pass");
        User user2 = new User("user2", "pass");
        db.save(user1);
        db.save(user2);

        Message msg1 = new Message(sessionInfo.user.getId(), user1.getId(), "message");
        Message msg2 = new Message(user2.getId(), sessionInfo.user.getId(), "message");
        db.save(msg1);
        db.save(msg2);

        Packet packet = sessionInfo.makePacket();
        Packet resp = handler.handle(packet, null);
        TestUtility.assertNotErrorPacket(resp);

        ObjectListPacket<User> userList = (ObjectListPacket<User>) resp;
        assertEquals(2, userList.getObjList().size());
        for (User user : userList.getObjList()) {
            assertTrue(user.getId() == user1.getId() || user.getId() == user2.getId());
        }
    }
}
