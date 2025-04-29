package client.screens;

import client.Client;
import data.Listing;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * CreateListingScreen Class. A class used to show gui for creating a listing.
 *
 * @author Karma Luitel, lab L24
 * @version 4/29/25
 */
public class CreateListingScreen extends Screen implements ICreateListingScreen {

    JTextField titleField;
    JTextArea descField;
    JTextField priceField;
    String imageHash;
    Component parent;
    JLabel imageLabel;


    public CreateListingScreen() {
        imageHash = "null";
        parent = this;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.add(Box.createVerticalGlue());
        Box box = new Box(BoxLayout.Y_AXIS);

        box.add(Box.createVerticalStrut(70));

        JLabel titleLabel = new JLabel("Title:");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleField = new JTextField(10);
        titleField.setMaximumSize(new Dimension(250, 200));
        box.add(titleLabel);
        box.add(titleField);
        box.add(Box.createVerticalStrut(25));
        JLabel descLabel = new JLabel("Description:");
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descField = new JTextArea(10, 10);
        JScrollPane descScroll = new JScrollPane(descField);
        descScroll.setMaximumSize(new Dimension(250, 300));
        box.add(descLabel);
        box.add(descScroll);
        box.add(Box.createVerticalStrut(25));
        JLabel priceLabel = new JLabel("Price:");
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        priceField = new JTextField(10);
        priceField.setMaximumSize(new Dimension(250, 200));
        box.add(priceLabel);
        box.add(priceField);
        box.add(Box.createVerticalStrut(25));

        imageLabel = new JLabel("");
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(imageLabel);
        box.add(Box.createVerticalStrut(25));

        JButton imageButton = new JButton("Upload Image");
        imageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jf = new JFileChooser();
                jf.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        if (f.isDirectory()) {
                            return true;
                        }
                        String name = f.getName().toLowerCase();
                        return ((name.endsWith(".png")) || (name.endsWith(".jpg")) || (name.endsWith(".jpeg")));
                    }

                    @Override
                    public String getDescription() {
                        return "PNG, JPG, and JPEG Images";
                    }
                });
                int opt = jf.showOpenDialog(parent);
                if (opt == JFileChooser.APPROVE_OPTION) {
                    File f = jf.getSelectedFile();
                    Client c = getClient();
                    imageHash = c.uploadImage(f);
                    if (imageHash != null) {
                        imageLabel.setText(f.getName());
                    } else {
                        imageHash = "null";
                    }

                }
            }
        });
        JButton createButton = new JButton("Create");
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = titleField.getText();
                String description = descField.getText();
                String price = priceField.getText();
                Client c = getClient();
                Listing l = c.createListing(title, description, price, imageHash);
                if (l != null) {
                    JOptionPane.showMessageDialog(parent, "Listing created successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(imageButton);
        buttonPanel.add(createButton);
        box.add(buttonPanel);

        Border b = BorderFactory.createLineBorder(Color.BLACK, 2, true);
        box.setPreferredSize(new Dimension(270, 500));
        box.setMaximumSize(new Dimension(270, 500));
        box.setBorder(b);

        box.add(Box.createVerticalStrut(100));
        this.add(box);
        this.add(Box.createVerticalGlue());
    }
}
