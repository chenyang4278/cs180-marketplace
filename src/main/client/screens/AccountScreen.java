package client.screens;

import client.Client;
import client.Styles;
import data.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * AccountScreen
 *
 * Account screen of our gui.
 *
 * @author Ayden Cline
 * @version 4/20/25
 */
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
        JButton setBalBtn = new JButton("Set balance (for testing):");

        JTextField balField = new JTextField(10);
        setBalBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (getClient().setUserBalance(balField.getText())) {
                    balanceLabel.setText("Balance: $" + getClient().getUser().getBalance());
                }
            }
        });

        topPanel.add(userLabel);
        topPanel.add(balanceLabel);

        centerPanel.add(logoutBtn);
        centerPanel.add(deleteAccountBtn);
        centerPanel.add(setBalBtn);
        centerPanel.add(balField);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * LogoutAction
     *
     * Private class listener for logout.
     *
     * @author Ayden Cline
     * @version 4/20/25
     */
    private class LogoutAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            getClient().logout();
            getGUI().setScreen(new HomeScreen());
        }
    }

    /**
     * DeleteAccountAction
     *
     * Private class listener for delete.
     *
     * @author Ayden Cline
     * @version 4/20/25
     */
    private class DeleteAccountAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (getClient().deleteUser()) {
                getGUI().setScreen(new HomeScreen());
            }
        }
    }
}
