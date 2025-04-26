package client.screens;

import client.Client;
import data.Message;
import data.User;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.IOException;
import java.util.*;

import java.text.SimpleDateFormat;

/**
 * MessageScreen
 * <p>
 * Organizes layout for the messaging GUI
 * keeps track of existing threads between 2 users and 
 * can make new DM with users by search.
 * 
 * @author Ian Ogden
 * @version 4/24/25
 */
public class MessageScreen extends Screen implements IMessageScreen {

    private JTextField recipientField;
    private JPanel chatLog;
    private JPanel messagesContainer;
    private JScrollPane chatLogScrollPane;
    private JTextArea messageInput;
    private JButton sendButton;
    private java.util.List<User> contacts;
    private JPanel userChats;
    private User recipient;

    public MessageScreen() {

        setLayout(new BorderLayout());

        // adds the recipient text field to the top of the screen
        recipientField = new JTextField();
        recipientField.setPreferredSize(new Dimension(0, 40));
        recipientField.setBorder(BorderFactory.createTitledBorder("To:"));
        recipientField.addActionListener(e -> {
            if (!recipientField.getText().trim().isEmpty()) {
                java.util.List<User> user = getClient().searchUsersByUsername(recipientField.getText(), false);

                if (user.size() == 0) {
                    messagesContainer.removeAll();
                    messagesContainer.revalidate();
                    messagesContainer.repaint();
                    recipient = null;
                } else if (user.size() > 1) {
                    JOptionPane.showMessageDialog(null,
                    "Something has gone wrong. Duplicate user exists.", "Error",
                    JOptionPane.ERROR_MESSAGE);
                } else {
                    recipient = user.get(0);
                    displayChatLog(recipient);
                }
            } else {
                messagesContainer.removeAll();
                messagesContainer.revalidate();
                messagesContainer.repaint();
                recipient = null;
            }
        });
        add(recipientField, BorderLayout.NORTH);
        
        // adds the chat log in the middle, below recipient field
        chatLog = new JPanel(new BorderLayout());
        chatLogScrollPane = new JScrollPane(chatLog);
        chatLogScrollPane.setPreferredSize(new Dimension(630, 420));
        chatLogScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        chatLogScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(chatLogScrollPane, BorderLayout.CENTER);

        messagesContainer = new JPanel();
        messagesContainer.setLayout(new BoxLayout(messagesContainer, BoxLayout.Y_AXIS));
        messagesContainer.setOpaque(false);
        chatLog.add(messagesContainer, BorderLayout.SOUTH);
        
        // adds the field to write messages as well as the send button to the bottom of the screen
        messageInput = new JTextArea(3, 20);
        messageInput.setLineWrap(true);
        messageInput.setWrapStyleWord(true);
        JScrollPane messageInputScrollPane = new JScrollPane(messageInput);

        sendButton = new JButton("↑");
        sendButton.setPreferredSize(new Dimension(40, 40));
        sendButton.addActionListener(e -> {
            String message = messageInput.getText();
            if (recipient != null && !message.trim().isEmpty()) {
                
                getClient().sendMessage(recipient.getId(), message);
                messageInput.setText("");
                displayChatLog(recipient);
                updateUserChats();

            }
        });

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageInputScrollPane, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        add(inputPanel, BorderLayout.SOUTH);

        // adds the users who have been in contact with the client on the left panel
        userChats = new JPanel();
        userChats.setLayout(new BoxLayout(userChats, BoxLayout.Y_AXIS));
        userChats.setPreferredSize(new Dimension(150, getHeight()));
        updateUserChats();
        add(userChats, BorderLayout.WEST);


    }

    public void updateUserChats() {
        contacts = getClient().getInboxUsers();
        userChats.removeAll();
        for (User user : contacts) userChats.add(new UserMessageButton(user));
        userChats.revalidate();
    }

    public void displayChatLog(User other) {

        messagesContainer.removeAll();

        java.util.List<Message> chats = getClient().getMessagesWithUser(other.getId());
        Collections.reverse(chats);
        for (Message each : chats) {
            addMessage(each.getMessage(), 
            getClient().searchUserById(each.getSenderId()).getUsername().equals(getClient().getUser().getUsername()));
        }

        SwingUtilities.invokeLater(() -> {
            chatLogScrollPane.getVerticalScrollBar().setValue(chatLogScrollPane.getVerticalScrollBar().getMaximum());
        });

    }

    public void addMessage(String text, boolean rightAlign) {

        MessageBubble mb = new MessageBubble(text, rightAlign);
        
        JPanel padder = new JPanel(new FlowLayout(rightAlign ? FlowLayout.RIGHT : FlowLayout.LEFT, 10, 5));
        padder.setOpaque(false);
        padder.add(mb);
        
        messagesContainer.add(padder);
        messagesContainer.revalidate();
        messagesContainer.repaint();

    }

    private class UserMessageButton extends JButton {

        public UserMessageButton(User user) {
            super(user.getUsername());
            if (getFontMetrics(getFont()).stringWidth(user.getUsername()) > 130) {
                setText(user.getUsername().substring(0,10) + "...");
            }
            this.setAlignmentX(Component.CENTER_ALIGNMENT);
            this.setMaximumSize(new Dimension(150, 40));
            this.addActionListener(e -> {
                recipientField.setText(user.getUsername());
                recipient = user;
                displayChatLog(user);
            });
        }

    }

}
