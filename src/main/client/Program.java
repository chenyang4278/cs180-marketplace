package client;

import client.screens.AccountScreen;
import client.screens.HomeScreen;

import javax.swing.*;
import java.io.IOException;

public class Program implements IProgram {
    private static ClientGUI gui;
    private static Client client;


    /*
    User accounts (Users are both Buyers and Sellers) - DONE
    New user account creation - DONE
    Password protected login - DONE
    Account deletion
    Item listing creation
    Item listing deletion
    Item search - ALMOST DONE
    Users can message an item's Seller - Sellers can respond to messages they receive.
    Payment processing
    User balance tracking
    Extra credit opportunity – Add support to upload and display item pictures.
     */

    public static void main(String[] args) throws IOException {
        client = new Client("localhost", 8080, true);

        SwingUtilities.invokeLater(() -> {
            gui = new ClientGUI();
            gui.setScreen(new HomeScreen());
        });
    }

    public static ClientGUI getGUI() {
        return gui;
    }

    public static Client getClient() {
        return client;
    }
}
