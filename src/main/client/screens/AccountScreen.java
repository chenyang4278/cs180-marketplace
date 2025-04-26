package client.screens;

import client.Client;
import client.Styles;
import data.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AccountScreen extends Screen implements IAccountScreen {
    public AccountScreen() {
        Client client = getClient();

        if (!client.isLoggedIn()) {
            getGUI().goBackScreen();
            return;
        }

        User user = client.getUser();

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        JPanel centerPanel = new JPanel();

        JLabel userLabel = new JLabel(user.getUsername());
        userLabel.setFont(Styles.TITLE_FONT);
        JLabel balanceLabel = new JLabel("Balance: $" + user.getBalance());
        balanceLabel.setFont(Styles.SUBTITLE_FONT);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(new LogoutAction());
        JButton deleteAccountBtn = new JButton("Delete account");
        deleteAccountBtn.addActionListener(new DeleteAccountAction());

        topPanel.add(userLabel);
        topPanel.add(balanceLabel);

        centerPanel.add(logoutBtn);
        centerPanel.add(deleteAccountBtn);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private class LogoutAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            getClient().logout();
            getGUI().setScreen(new HomeScreen());
        }
    }

    private class DeleteAccountAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (getClient().deleteUser()) {
                getGUI().setScreen(new HomeScreen());
            }
        }
    }
}
