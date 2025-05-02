package client;

import client.screens.AccountScreen;
import client.screens.HomeScreen;
import client.screens.ListingsScreen;

import javax.swing.*;
import java.io.IOException;

public class Program implements IProgram {
    private static ClientGUI gui;
    private static Client client;


    /*
    User accounts (Users are both Buyers and Sellers) - DONE
    New user account creation - DONE
    Password protected login - DONE
    Account deletion - DONE
    Item listing creation - DONE
    Item listing deletion - DONE
    Item search - DONE
    Users can message an item's Seller - Sellers can respond to messages they receive. - DONE
    Payment processing - DONE
    User balance tracking - DONE
    Extra credit opportunity – Add support to upload and display item pictures. - DONE
     */

    public static void main(String[] args) throws IOException {
        client = new Client("localhost", 8080, true);

        SwingUtilities.invokeLater(() -> {
            gui = new ClientGUI();
            gui.setScreen(new HomeScreen());
            Program.getGUI().setScreen(new ListingsScreen());
        });
    }

    public static ClientGUI getGUI() {
        return gui;
    }

    public static Client getClient() {
        return client;
    }
}
