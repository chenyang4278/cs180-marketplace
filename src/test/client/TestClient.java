package client;

import org.junit.Before;
import org.junit.Test;

import data.Listing;
import data.Message;
import packet.ErrorPacketException;
import packet.PacketHeader;
import packet.PacketParsingException;
import server.ClientHandler;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * TestClient
 * <p>
 * A class that runs JUnit tests on methods in the Client.java class.
 * 
 * @author Ian Ogden
 * @version 4/16/25
 */
public class TestClient {

    /*
     * TestServer just acts as a class to keep a server running in the background 
     * in order for the clients to connect to during tests.
     */
    class TestServer implements Runnable {

        @Override
        public void run() {
            try {
                ExecutorService pool = Executors.newCachedThreadPool();
                ServerSocket server = new ServerSocket(12345);

                while (true) {
                    Socket socket = server.accept();
                    pool.submit(new ClientHandler(socket));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
    }
    @Before
    public void setUpServer() {
        Thread serverThread = new Thread(new TestServer());
        serverThread.start();
    }

    @Test
    public void testClientInitialization() throws IOException {
        
        Client client = new Client("localhost", 12345);
        client.setCurrentUserId(100);
        assertEquals(100, client.getCurrentUserId());

    }

    @Test
    public void testClose() throws IOException {
        Client client = new Client("localhost", 12345);
        client.close();
        boolean b = client.createUser("test", "1234");
        assertFalse(b);
    }

    @Test
    public void testUserCreationLogin() throws IOException {
        
        String user = "testingusername";
        Client client = new Client("localhost", 12345);
        
        // make sure that user with such info does not exist in User.csv already
        assertTrue(client.createUser(user, "3.1415"));

        assertTrue(client.login(user, "3.1415"));
        assertEquals(user, client.getUser().getUsername());

        Client c2 = new Client("localhost", 12345);
        c2.createUser(user, "3.1415");
        // shouldn't assign c2 a user since username is already taken
        assertNull(c2.getUser());

        client.deleteUser();
        c2.deleteUser();

    }

    @Test
    public void testCreateHeaders() {

        List<PacketHeader> headers = Client.createHeaders();
        assertEquals(0, headers.size());
        
        headers = Client.createHeaders("key1", "value1", "key2", "value2");
        assertEquals(2, headers.size());
        
        PacketHeader ph1 = headers.get(0);
        PacketHeader ph2 = headers.get(1);

        assertTrue("key1".equals(ph1.getName()));
        assertTrue("key2".equals(ph2.getName()));
        
        assertTrue("value1".equals(ph1.getValues().get(0)));
        assertTrue("value2".equals(ph2.getValues().get(0)));
        
    }

    @Test
    public void testSetUserBal() throws IOException {

        Client c = new Client("localhost", 12345);
        c.createUser("user", "pass");
        c.login("user", "pass");

        c.setUserBalance(1000);
        assertEquals(1000, c.getUser().getBalance(), 0.01);

        c.deleteUser();

    }

    @Test
    public void testCreateAndBuyListing() throws IOException {

        Client seller = new Client("localhost", 12345);
        seller.createUser("ian", "pass");
        seller.login("ian", "pass");
        
        Listing item = seller.createListing("apple", "red, crunchy", 5, "null");

        Client buyer = new Client("localhost", 12345);
        buyer.createUser("buyeruser", "password");
        buyer.login("buyeruser", "password");
        
        buyer.setUserBalance(3);
        // should be false since balance 
        assertFalse(buyer.buyListing(item.getListingId()));

        buyer.setUserBalance(10);
        // now balance is sufficient, should return true
        assertTrue(buyer.buyListing(item.getListingId()));

        // after buying, the balance should reflect the purchase.
        assertEquals(10-5, buyer.getUser().getBalance(), 0.01);

        Client otherbuyer = new Client("localhost", 12345);
        otherbuyer.createUser("buyer2", "pw");
        otherbuyer.login("buyer2", "pw");

        otherbuyer.setUserBalance(100000);
        // once item is sold, nobody else should be able to purchase
        assertFalse(otherbuyer.buyListing(item.getListingId()));
        assertEquals(100000, otherbuyer.getUser().getBalance(), 0.01);

        seller.deleteListing(item.getListingId());
        seller.deleteUser();
        buyer.deleteUser();
        otherbuyer.deleteUser();

    }

    @Test
    public void testDeleteUser() throws IOException {

        Client client = new Client("localhost", 12345);
        client.createUser("ian", "pass");
        client.login("ian", "pass");
        // make sure client is assigned new user
        assertEquals("ian", client.getUser().getUsername());
        assertTrue(client.deleteUser());
        // make sure client is no longer storing old user
        assertNull(client.getUser());
        // make sure old user was deleted from User.csv by trying to make a new user with same username
        assertTrue(client.createUser("ian", "pass"));
        client.login("ian", "pass");
        // delete user again
        assertTrue(client.deleteUser());
    }

    @Test
    public void testDeleteListing() throws IOException {

        Client c = new Client("localhost", 12345);
        c.createUser("seller", "password");
        c.login("seller", "password");

        Listing item = c.createListing("necklace", "pearl", 100, "null");
        int listingid = item.getListingId();
        c.deleteListing(listingid);

        Client c2 = new Client("localhost", 12345);
        c2.createUser("buyer", "pass");
        c2.login("buyer", "pass");
        c2.setUserBalance(100000);
        assertFalse(c2.buyListing(listingid));

        c.deleteUser();
        c2.deleteUser();

    }

    @Test
    public void testSearchListings() throws IOException {

        System.out.println("debug:");

        Client seller = new Client("localhost", 12345);
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

        seller.deleteListing(item1.getListingId());
        seller.deleteListing(item2.getListingId());
        seller.deleteUser();
            
    }

    @Test
    public void testMessaging() throws IOException {

        Client c1 = new Client("localhost", 12345);
        c1.createUser("Alice", "pass");
        c1.login("Alice", "pass");

        Client c2 = new Client("localhost", 12345);
        c2.createUser("Bob", "pass");
        c2.login("Bob", "pass");

        assertTrue(c1.sendMessage(c1.getUser().getId(), c2.getUser().getId(), "Hi Bob!"));

        List<Message> history = c2.getMessagesWithUser(c1.getUser().getId());
        assertEquals(1, history.size());
        assertEquals("Hi Bob!", history.get(0).getMessage());

        assertTrue(c2.sendMessage(c2.getUser().getId(), c1.getUser().getId(), "What's up Alice?"));
        history = c1.getMessagesWithUser(c2.getUser().getId());
        assertEquals(2, history.size());
        
        c1.deleteUser();
        c2.deleteUser();

    }

}
