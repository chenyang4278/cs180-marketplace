package client;

import data.User;
import database.DatabaseWrapper;
import database.DatabaseWriteException;
import org.junit.BeforeClass;
import org.junit.Test;

import data.Listing;
import data.Message;
import packet.PacketHeader;
import server.Server;
import server.handlers.HandlerUtil;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * TestClient
 * <p>
 * A class that runs JUnit tests on methods in the Client.java class.
 * 
 * @author Ian Ogden
 * @version 4/16/25
 */
public class TestClient {

    /**
     * TestServer
     *  * <p>
     * TestServer just acts as a class to keep a server running in the background 
     * in order for the clients to connect to during tests.
     *
     * @author Ian Ogden
     * @version 4/16/25
     */
    static class TestServer implements Runnable {
        @Override
        public void run() {
            try {
                Server.main(new String[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @BeforeClass
    public static void setUpServer() {
        Thread serverThread = new Thread(new TestServer());
        serverThread.start();
    }

    @Test
    public void testClientInitialization() throws IOException {
        
        Client client = new Client("localhost", 8080, false);
        client.setCurrentUserId(100);
        assertEquals(100, client.getCurrentUserId());

        client.close();
    }

    @Test
    public void testClose() throws IOException {
        Client client = new Client("localhost", 8080, false);
        client.close();
        assertNull(client.createUser("test", "1234"));
    }

    @Test
    public void testUserCreationLogin() throws IOException {
        
        String user = "testingusername";
        Client client = new Client("localhost", 8080, false);
        
        // make sure that user with such info does not exist in User.csv already
        assertNotNull(client.createUser(user, "3.1415"));

        assertTrue(client.login(user, "3.1415"));
        assertEquals(user, client.getUser().getUsername());

        Client c2 = new Client("localhost", 8080, false);
        c2.createUser(user, "3.1415");
        // shouldn't assign c2 a user since username is already taken
        assertNull(c2.getUser());

        client.deleteUser();
        c2.deleteUser();

        client.close();
        c2.close();
    }

    @Test
    public void testCreateHeaders() {

        List<PacketHeader> headers = Client.createHeaders();
        assertEquals(0, headers.size());
        
        headers = Client.createHeaders("key1", "value1", "key2", "value2");
        assertEquals(2, headers.size());
        
        PacketHeader ph1 = headers.get(0);
        PacketHeader ph2 = headers.get(1);

        assertEquals("key1", ph1.getName());
        assertEquals("key2", ph2.getName());

        assertEquals("value1", ph1.getValues().get(0));
        assertEquals("value2", ph2.getValues().get(0));
        
    }

    @Test
    public void testSetUserBal() throws IOException {

        Client c = new Client("localhost", 8080, false);
        c.createUser("user", "pass");
        c.login("user", "pass");

        c.setUserBalance(1000);
        assertEquals(1000, c.getUser().getBalance(), 0.01);

        c.deleteUser();

        c.close();
    }

    @Test
    public void testCreateAndBuyListing() throws IOException {

        Client seller = new Client("localhost", 8080, false);
        seller.createUser("ian", "pass");
        seller.login("ian", "pass");
        
        Listing item = seller.createListing("apple", "red, crunchy", 5, "null");

        Client buyer = new Client("localhost", 8080, false);
        buyer.createUser("buyeruser", "password");
        buyer.login("buyeruser", "password");
        
        buyer.setUserBalance(3);
        // should be false since balance 
        assertFalse(buyer.buyListing(item.getId()));

        buyer.setUserBalance(10);
        // now balance is sufficient, should return true
        assertTrue(buyer.buyListing(item.getId()));

        // after buying, the balance should reflect the purchase.
        assertEquals(10 - 5, buyer.getUser().getBalance(), 0.01);

        Client otherbuyer = new Client("localhost", 8080, false);
        otherbuyer.createUser("buyer2", "pw");
        otherbuyer.login("buyer2", "pw");

        otherbuyer.setUserBalance(100000);
        // once item is sold, nobody else should be able to purchase
        assertFalse(otherbuyer.buyListing(item.getId()));
        assertEquals(100000, otherbuyer.getUser().getBalance(), 0.01);

        seller.deleteListing(item.getId());
        seller.deleteUser();
        buyer.deleteUser();
        otherbuyer.deleteUser();

        seller.close();
        buyer.close();
        otherbuyer.close();
    }

    @Test
    public void testDeleteUser() throws IOException {

        Client client = new Client("localhost", 8080, false);
        client.createUser("ian", "pass");
        client.login("ian", "pass");
        // make sure client is assigned new user
        assertEquals("ian", client.getUser().getUsername());
        assertTrue(client.deleteUser());
        // make sure client is no longer storing old user
        assertNull(client.getUser());
        // make sure old user was deleted from User.csv by trying to make a new user with same username
        assertNotNull(client.createUser("ian", "pass"));
        client.login("ian", "pass");
        // delete user again
        assertTrue(client.deleteUser());

        client.close();
    }

    @Test
    public void testDeleteListing() throws IOException {

        Client c = new Client("localhost", 8080, false);
        c.createUser("seller", "password");
        c.login("seller", "password");

        Listing item = c.createListing("necklace", "pearl", 100, "null");
        int listingid = item.getId();
        c.deleteListing(listingid);

        Client c2 = new Client("localhost", 8080, false);
        c2.createUser("buyer", "pass");
        c2.login("buyer", "pass");
        c2.setUserBalance(100000);
        assertFalse(c2.buyListing(listingid));

        c.deleteUser();
        c2.deleteUser();

        c.close();
        c2.close();
    }

    @Test
    public void testSearchListings() throws IOException {

        System.out.println("debug:");

        Client seller = new Client("localhost", 8080, false);
        seller.createUser("seller", "pass");
        seller.login("seller", "pass");
        Listing item1 = seller.createListing("item1", "description1", 10, "null");
        Listing item2 = seller.createListing("item2", "description2", 12, "null");
        
        List<Listing> list = seller.searchListingsByAttribute("image", "null");
        assertEquals(2, list.size());
        assertEquals(item1.toString(), list.get(0).toString());
        assertEquals(item2.toString(), list.get(1).toString());

        list = seller.searchListingsByAttribute("title", "item");
        assertEquals(0, list.size());

        list = seller.searchListingsByAttribute("price", "12.0");
        assertEquals(1, list.size());
        assertEquals(item2.toString(), list.get(0).toString());

        list = seller.searchListingsByAttribute("sellerName", seller.getUser().getUsername());
        assertEquals(2, list.size());

        seller.deleteListing(item1.getId());
        seller.deleteListing(item2.getId());
        seller.deleteUser();

        seller.close();
    }

    @Test
    public void testMessaging() throws IOException {

        Client c1 = new Client("localhost", 8080, false);
        c1.createUser("Alice", "pass");
        c1.login("Alice", "pass");

        Client c2 = new Client("localhost", 8080, false);
        c2.createUser("Bob", "pass");
        c2.login("Bob", "pass");

        assertTrue(c1.sendMessage(c2.getUser().getId(), "Hi Bob!"));

        List<Message> history = c2.getMessagesWithUser(c1.getUser().getId());
        assertEquals(1, history.size());
        assertEquals("Hi Bob!", history.get(0).getMessage());

        assertTrue(c2.sendMessage(c1.getUser().getId(), "What's up Alice?"));
        history = c1.getMessagesWithUser(c2.getUser().getId());
        assertEquals(2, history.size());
        
        c1.deleteUser();
        c2.deleteUser();

        c1.close();
        c2.close();
    }

    private String calculateFileHash(File file) throws IOException {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) { // should not occur
            throw new RuntimeException(e);
        }

        FileInputStream fos = new FileInputStream(file);
        byte[] buf = new byte[1024 * 1024];
        int count;
        while ((count = fos.read(buf)) > 0) {
            digest.update(buf, 0, count);
        }

        fos.close();

        return HandlerUtil.hex(digest.digest());
    }

    @Test
    public void testImageUploadAndDownload() throws IOException {
        File testFile = new File("README.md");

        Client client = new Client("localhost", 8080, false);
        client.createUser("Alice", "pass");
        client.login("Alice", "pass");

        String fileHash = client.uploadImage(testFile);
        assertNotNull(fileHash);

        File staticFile = new File("static/" + fileHash);
        assertTrue(staticFile.exists());

        assertEquals(calculateFileHash(testFile), calculateFileHash(staticFile));

        File downloadedFile = client.downloadImage("invalid hash");
        assertNull(downloadedFile);

        downloadedFile = client.downloadImage(fileHash);
        assertNotNull(downloadedFile);

        assertEquals(calculateFileHash(testFile), calculateFileHash(downloadedFile));
    }

    @Test
    public void testGetInboxUsers() throws IOException, DatabaseWriteException {
        Client client = new Client("localhost", 8080, false);
        User me = client.createUser("user1", "pass");
        User user2 = client.createUser("user2", "pass");
        User user3 = client.createUser("user3", "pass");
        client.login("user1", "pass");

        DatabaseWrapper db = DatabaseWrapper.get();

        Message msg1 = new Message(me.getId(), user2.getId(), "message");
        Message msg2 = new Message(user3.getId(), me.getId(), "message");
        db.save(msg1);
        db.save(msg2);

        List<User> users = client.getInboxUsers();
        assertEquals(2, users.size());
        for (User user : users) {
            assertTrue(user.getId() == user2.getId() || user.getId() == user3.getId());
        }
    }
}
