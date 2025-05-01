package client.screens;

import client.Program;
import data.Listing;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * ViewListingPopup
 *
 * Displays a listing with title, price, description, and image (if available).
 *
 * @author Chen
 * @version 4/27/25
 */
public class ViewListingPopup extends JFrame {

    public ViewListingPopup(Listing listing) {
        setTitle("Listing Details");
        setSize(400, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel(listing.getTitle());
        titleLabel.setFont(new Font("Serif", Font.BOLD, 22));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel priceLabel = new JLabel("Price: $" + listing.getPrice());
        priceLabel.setFont(new Font("Serif", Font.PLAIN, 18));
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea descriptionArea = new JTextArea(listing.getDescription());
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        descriptionArea.setBorder(BorderFactory.createTitledBorder("Description"));

        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setPreferredSize(new Dimension(350, 150));

        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(priceLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        if (!listing.getImage().equals("null")) {
            File imgFile = Program.getClient().downloadImage(listing.getImage());
            if (imgFile != null && imgFile.exists()) {
                ImageIcon img = new ImageIcon(imgFile.getAbsolutePath());
                Image scaled = img.getImage().getScaledInstance(300, 200, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(scaled));
                imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                panel.add(imageLabel);
                panel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        panel.add(descScroll);

        add(panel);
    }
}
