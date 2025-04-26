package client;

import client.screens.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Header extends JPanel implements IHeader {
    private final JButton loginBtn;
    private final JButton accountBtn;
    private final JButton messageBtn;

    public Header() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        JButton homeBtn = createLinkButton("Home", "home");
        JButton listingBtn = createLinkButton("Listings", "listings");
        messageBtn = createLinkButton("Message", "message");
        loginBtn = createLinkButton("Login", "login");
        accountBtn = createLinkButton("Account", "account");

        add(homeBtn);
        add(listingBtn);
        add(messageBtn);
        add(Box.createHorizontalGlue());
        add(loginBtn);
        add(accountBtn);
    }

    public void refresh() {
        Client client = Program.getClient();
        if (client.isLoggedIn()) {
            messageBtn.setVisible(true); 
            loginBtn.setVisible(false);
            accountBtn.setVisible(true);
        } else {
            messageBtn.setVisible(false);
            loginBtn.setVisible(true);
            accountBtn.setVisible(false);
        }
    }

    private JButton createLinkButton(String text, String actionCmd) {
        JButton button = new JButton(text);
        button.setActionCommand(actionCmd);
        button.addActionListener(new LinkAction());
        return button;
    }

    private static class LinkAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Screen screen = switch (e.getActionCommand()) {
                case "home": yield new HomeScreen();
                case "listings": yield new ListingsScreen();
                case "message": yield new MessageScreen();
                case "account": yield new AccountScreen();
                case "login": yield new LoginScreen();
                default: throw new RuntimeException(
                    "Invalid command: " + e.getActionCommand() + ". Did you make a typo?"
                );
            };
            Program.getGUI().setScreen(screen);
        }
    }
}
