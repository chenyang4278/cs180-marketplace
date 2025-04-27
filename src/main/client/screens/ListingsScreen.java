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
import java.util.ArrayList;
import java.awt.event.*;

public class ListingsScreen extends Screen implements IListingsScreen {

    private String searchTag;
    private JTextField searchField;
    private JPanel listingGrid;

    public ListingsScreen() {
        searchTag = "sellerName";
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(getSearchBar());
        listingGrid = new JPanel();
        listingGrid.setLayout(new GridLayout(0, 5, 10, 10));
        add(listingGrid);
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
        searchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Client client = getClient();
                ArrayList<Listing> listings = (ArrayList<Listing>) client.searchListingsByAttribute(searchTag, searchField.getText());
                addListingsToGrid(listings);
            }
        });

        searchBar.add(searchBtn);
        searchBar.setAlignmentX(Component.CENTER_ALIGNMENT);

        return searchBar;
    }


    //TODO: unfinished
    private void addListingsToGrid(ArrayList<Listing> l) {
        listingGrid.removeAll();
        System.out.println(l.size());
        for (Listing list : l) {
            JPanel jpOuter = new JPanel();
            JPanel jpInner = new JPanel();

            Border b = BorderFactory.createLineBorder(Color.BLACK, 1, true);
            jpInner.setPreferredSize(new Dimension(200, 200));
            jpInner.setMaximumSize(new Dimension(200, 200));
            jpInner.setBorder(b);
            jpInner.setLayout(new BoxLayout(jpInner, BoxLayout.Y_AXIS));
            jpInner.add(new JLabel(list.getTitle()));

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
                    Client client = getClient();
                    ArrayList<Listing> listings = (ArrayList<Listing>) client.searchListingsByAttribute(searchTag, searchField.getText());
                    addListingsToGrid(listings);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete listing.");
                }
            });
            popup.add(deleteItem);
        } else {
            JMenuItem buyItem = new JMenuItem("Buy Now");
            buyItem.addActionListener(e -> {
                boolean success = Program.getClient().buyListing(listing.getId());
                if (success) {
                    JOptionPane.showMessageDialog(this, "Purchase successful.");
                    Client client = getClient();
                    ArrayList<Listing> listings = (ArrayList<Listing>) client.searchListingsByAttribute(searchTag, searchField.getText());
                    addListingsToGrid(listings);
                } else {
                    JOptionPane.showMessageDialog(this, "Purchase failed.");
                }
            });
            popup.add(buyItem);
        }

        popup.show(this, MouseInfo.getPointerInfo().getLocation().x - getLocationOnScreen().x,
                MouseInfo.getPointerInfo().getLocation().y - getLocationOnScreen().y);
    }
}
