package client;

import client.screens.AccountScreen;
import client.screens.HomeScreen;
import client.screens.ListingsScreen;
import client.screens.Screen;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Header extends JPanel implements IHeader {
    public Header() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        LinkAction linkAction = new LinkAction();
        JButton homeBtn = new JButton("Home");
        JButton listingBtn = new JButton("Listings");
        JButton accountBtn = new JButton("Account");
        homeBtn.setActionCommand("home");
        listingBtn.setActionCommand("listings");
        accountBtn.setActionCommand("account");
        homeBtn.addActionListener(linkAction);
        listingBtn.addActionListener(linkAction);
        accountBtn.addActionListener(linkAction);

        add(homeBtn);
        add(listingBtn);
        add(Box.createHorizontalGlue());
        add(accountBtn);
    }

    private static class LinkAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Screen screen = switch (e.getActionCommand()) {
                case "home": yield new HomeScreen();
                case "listings": yield new ListingsScreen();
                case "account": yield new AccountScreen();
                default: throw new RuntimeException(
                    "Invalid command: " + e.getActionCommand() + ". Did you make a typo?"
                );
            };
            Program.getGUI().setScreen(screen);
        }
    }
}
