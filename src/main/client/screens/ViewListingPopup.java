package client.screens;

import client.Program;
import data.Listing;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;

public class ViewListingPopup extends JFrame {

    public ViewListingPopup(Listing listing) {
        setTitle("Listing Details");
        setSize(400, 500);
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

        // Try to load image if available
        /*if (listing.getImageHash() != null && !listing.getImageHash().isEmpty()) {
            try {
                byte[] imgBytes = Program.getClient().downloadImage(listing.getImageHash());
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(imgBytes));
                if (img != null) {
                    ImageIcon icon = new ImageIcon(img.getScaledInstance(300, 200, Image.SCALE_SMOOTH));
                    JLabel imageLabel = new JLabel(icon);
                    imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    panel.add(imageLabel);
                    panel.add(Box.createRigidArea(new Dimension(0, 10)));
                }
            } catch (Exception e) {
                System.out.println("Image load failed: " + e.getMessage());
            }
        }

         */

        panel.add(descScroll);

        add(panel);
    }
}
