package client;

import client.screens.AccountScreen;
import client.screens.HomeScreen;

import javax.swing.*;
import java.io.IOException;

public class Program implements IProgram {
    private static ClientGUI gui;
    private static Client client;

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
