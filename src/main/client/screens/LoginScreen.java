package client.screens;
import client.Client;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginScreen extends Screen implements ILoginScreen {


    //note for myself later, use this link for boxlayout info: https://docs.oracle.com/javase/tutorial/uiswing/layout/box.html
    private Client client;

    public LoginScreen() {
        client = getClient();

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.add(Box.createVerticalGlue());
        this.add(createInputBox("Login", "Login", "Create Account"));
        //this.add(createInputBox("Create Account", "Create", "To Login"));
        this.add(Box.createVerticalGlue());
    }

    private Box createInputBox(String t, String b1, String b2) {
        Box box = new Box(BoxLayout.Y_AXIS);

        JLabel title = new JLabel(t);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        box.add(Box.createVerticalStrut(10));
        box.add(title);

        box.add(Box.createVerticalStrut(70));

        JLabel userLabel = new JLabel("Username:");
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField userField = new JTextField(10);
        userField.setMaximumSize(new Dimension(250, 200));
        box.add(userLabel);
        box.add(userField);
        box.add(Box.createVerticalStrut(25));
        JLabel passLabel = new JLabel("Password:");
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPasswordField passField = new JPasswordField(10);
        passField.setMaximumSize(new Dimension(250, 200));
        box.add(passLabel);
        box.add(passField);
        box.add(Box.createVerticalStrut(25));
        JButton loginButton = new JButton(b1);
        box.add(loginButton);
        JButton createButton = new JButton(b2);
        box.add(loginButton);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(createButton);
        box.add(buttonPanel);

        Border b = BorderFactory.createLineBorder(Color.BLACK, 2, true);
        box.setPreferredSize(new Dimension(270, 380));
        box.setMaximumSize(new Dimension(270, 380));
        box.setBorder(b);

        box.add(Box.createVerticalStrut(100));

        return box;
    }
}
