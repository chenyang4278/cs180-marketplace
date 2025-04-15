package client;

import packet.ErrorPacketException;
import packet.PacketParsingException;

import java.io.IOException;

public class ClientExample {
    public static void main(String[] args) throws IOException, PacketParsingException, ErrorPacketException {
        Client c = new Client("localhost", 8080);
        c.createUser("karma", "12345");
        c.login(c.getCurrentUser().getUsername(), c.getCurrentUser().getPassword());
        c.createListing("apple watch", "a apple watch", 100.25, "null");

        Client c2 = new Client("localhost", 8080);
        c2.createUser("testuser", "12345");
        c2.login(c2.getCurrentUser().getUsername(), c2.getCurrentUser().getPassword());


    }
}
