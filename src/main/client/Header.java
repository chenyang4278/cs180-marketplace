package client;

import client.screens.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Header
 *
 * Serves as the top menu bar for our program.
 *
 * @author Ayden Cline
 * @version 4/20/25
 */
public class Header extends JPanel implements IHeader {
    private final JButton loginBtn;
    private final JButton accountBtn;
    private final JButton messageBtn;
    private final JButton createListingBtn;

    public Header() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        JButton homeBtn = createLinkButton("Home", "home");
        JButton listingBtn = createLinkButton("Listings", "listings");
        createListingBtn = createLinkButton("Create Listing", "listingCreate");
        messageBtn = createLinkButton("Message", "message");
        loginBtn = createLinkButton("Login", "login");
        accountBtn = createLinkButton("Account", "account");

        add(homeBtn);
        add(listingBtn);
        add(createListingBtn);
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
            createListingBtn.setVisible(true);
        } else {
            messageBtn.setVisible(false);
            loginBtn.setVisible(true);
            accountBtn.setVisible(false);
            createListingBtn.setVisible(false);
        }
    }

    private JButton createLinkButton(String text, String actionCmd) {
        JButton button = new JButton(text);
        button.setActionCommand(actionCmd);
        button.addActionListener(new LinkAction());
        return button;
    }

    /**
     * LinkAction
     *
     * Private ActionListener class for switching screens.
     *
     * @author Ayden Cline
     * @version 4/20/25
     */
    private static class LinkAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Screen screen = switch (e.getActionCommand()) {
                case "home": yield new HomeScreen();
                case "listings": yield new ListingsScreen();
                case "listingCreate": yield new CreateListingScreen();
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
