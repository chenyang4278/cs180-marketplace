package client.screens;

import client.Client;
import client.Program;
import data.Listing;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.awt.event.*;

/**
 * ListingsScreen
 *
 * Displays listings in a grid with search, view, delete, and buy functionality.
 * Images are shown if available. Includes a refresh button.
 *
 * @author Chen Yang, 24
 * @version 4/27/25
 */

public class ListingsScreen extends Screen implements IListingsScreen {

    private String searchTag;
    private JTextField searchField;
    private JPanel listingGrid;
    private ArrayList<File> images;
    private JLabel noResultsLabel;

    public ListingsScreen() {
        images = new ArrayList<>();
        searchTag = "sellerName";
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(getSearchBar());
        listingGrid = new JPanel();
        listingGrid.setLayout(new GridLayout(0, 5, 10, 10));
        add(listingGrid);

        noResultsLabel = new JLabel("No listings found.", SwingConstants.CENTER);
        noResultsLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        noResultsLabel.setForeground(Color.GRAY);
    }

    private JPanel getSearchBar() {
        JPanel searchBar = new JPanel();
        searchBar.add(new JLabel("Tag:"));

        JComboBox<String> tags = new JComboBox<>(new String[] {"sellerName", "title",
            "description", "price", "sold"});
        tags.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    searchTag = (String) tags.getSelectedItem();
                }
            }
        });
        searchBar.add(tags);
        searchBar.add(new JLabel("Query:"));
        searchField = new JTextField(10);
        searchBar.add(searchField);
        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> refreshGrid());
        searchBar.add(searchBtn);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshGrid());
        searchBar.add(refreshBtn);

        searchBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        return searchBar;
    }

    private void refreshGrid() {
        Client client = getClient();
        ArrayList<Listing> listings = (ArrayList<Listing>) client.searchListingsByAttribute(searchTag, searchField.getText());
        addListingsToGrid(listings);
    }


    //TODO: Can make this method MUCH more efficient
    private void addListingsToGrid(ArrayList<Listing> l) {
        listingGrid.removeAll();
        for (File f : images) {
            f.delete();
        }
        images.clear();
        boolean found = false;

        for (Listing list : l) {
            if (searchTag.equals("sold") || !list.isSold()) {
                found = true;
                JPanel jpOuter = new JPanel();
                JPanel jpInner = new JPanel();
                Border b = BorderFactory.createLineBorder(Color.BLACK, 1, true);
                jpInner.setPreferredSize(new Dimension(250, 250));
                jpInner.setMaximumSize(new Dimension(250, 250));
                jpInner.setBorder(b);
                jpInner.setLayout(new BoxLayout(jpInner, BoxLayout.Y_AXIS));

                JLabel title = new JLabel(list.getTitle());
                title.setAlignmentX(Component.CENTER_ALIGNMENT);
                jpInner.add(Box.createVerticalStrut(10));
                jpInner.add(title);

                JLabel price = new JLabel("$" + list.getPrice());
                price.setAlignmentX(Component.CENTER_ALIGNMENT);
                jpInner.add(Box.createVerticalStrut(5));
                jpInner.add(price);

                File imgFile = getClient().downloadImage(list.getImage());
                if (imgFile != null && imgFile.exists()) {
                    images.add(imgFile);
                    ImageIcon img = new ImageIcon(imgFile.getAbsolutePath());
                    Image scaled = img.getImage().getScaledInstance(190, 190, Image.SCALE_SMOOTH);
                    JLabel imageLabel = new JLabel(new ImageIcon(scaled));
                    imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    jpInner.add(Box.createVerticalStrut(5));
                    jpInner.add(imageLabel);
                }

                jpOuter.add(jpInner);
                listingGrid.add(jpOuter);

                jpInner.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        jpInner.setBackground(Color.LIGHT_GRAY);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        jpInner.setBackground(null);
                    }

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        showListingOptions(list);
                    }
                });
            }
        }
        if (!found) {
            listingGrid.setLayout(new BorderLayout());
            listingGrid.add(noResultsLabel, BorderLayout.CENTER);
        }
        listingGrid.revalidate();
        listingGrid.repaint();
    }

    private void showListingOptions(Listing listing) {
        JPopupMenu popup = new JPopupMenu();

        JMenuItem viewItem = new JMenuItem("View Listing");
        viewItem.addActionListener(e -> {
            ViewListingPopup popupWindow = new ViewListingPopup(listing);
            popupWindow.setVisible(true);
        });
        popup.add(viewItem);

        if (listing.getSellerId() == Program.getClient().getUser().getId()) {
            JMenuItem deleteItem = new JMenuItem("Delete Listing");
            deleteItem.addActionListener(e -> {
                boolean success = Program.getClient().deleteListing(listing.getId());
                if (success) {
                    JOptionPane.showMessageDialog(this, "Listing deleted.");
                    refreshGrid();
                } //dont need to account for failure since handler error will be displayed.
            });
            popup.add(deleteItem);
        } else {
            JMenuItem buyItem = new JMenuItem("Buy Now");
            buyItem.addActionListener(e -> {
                boolean success = Program.getClient().buyListing(listing.getId());
                if (success) {
                    JOptionPane.showMessageDialog(this, "Purchase successful.");
                    refreshGrid();
                } //dont need to account for failure since handler error will be displayed.
            });
            popup.add(buyItem);
        }

        popup.show(this, MouseInfo.getPointerInfo().getLocation().x - getLocationOnScreen().x,
                MouseInfo.getPointerInfo().getLocation().y - getLocationOnScreen().y);
    }
}

