package client.screens;

import client.Client;
import data.User;

import javax.swing.*;

public class AccountScreen extends Screen implements IAccountScreen {
    public AccountScreen() {
        Client client = getClient();

        if (!client.isLoggedIn()) {
            getGUI().goBackScreen();
            return;
        }

        User user = client.getUser();

        JPanel topPanel = new JPanel();
        JPanel centerPanel = new JPanel();

        JLabel userLabel = new JLabel(user.getUsername());

        topPanel.add(userLabel);

        add(topPanel);
        add(centerPanel);
    }
}
