package client;

import data.Listing;
import packet.ErrorPacketException;
import packet.PacketParsingException;

import java.io.IOException;

//TODO: Remove this class?
public class ClientExample {
    public static void main(String[] args) throws IOException, PacketParsingException, ErrorPacketException {
        Client c = new Client("localhost", 8080);
        c.createUser("karma4", "12345");
        c.login("karma4", "12345");
        Listing l = c.createListing("apple watch", "a apple watch", 100.25, "null");

        Client c2 = new Client("localhost", 8080);
        c2.createUser("testuser1", "12345");
        c2.login("testuser1", "12345");
        c2.setUserBalance(500);

        c2.buyListing(l.getId());

        System.out.println(c2.getUser().getBalance());
        System.out.println(c.getUser().getBalance());

        //should error
        Client c3 = new Client("localhost", 8080);
        c3.createUser("testuser1", "12345");
        c3.login("testuser1", "12345");


    }
}
