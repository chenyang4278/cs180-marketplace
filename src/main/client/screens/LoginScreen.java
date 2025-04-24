package client.screens;
import client.Client;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginScreen extends Screen implements ILoginScreen {


    //note for myself later, use this link for boxlayout info: https://docs.oracle.com/javase/tutorial/uiswing/layout/box.html
    private boolean loginMode;
    private JLabel title;
    private JButton switchButton;
    private JButton continueButton;
    private JTextField userField;
    private JPasswordField passField;

    public LoginScreen() {
        loginMode = true;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.add(Box.createVerticalGlue());
        Box box = new Box(BoxLayout.Y_AXIS);

        title = new JLabel("Login");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        box.add(Box.createVerticalStrut(10));
        box.add(title);

        box.add(Box.createVerticalStrut(70));

        JLabel userLabel = new JLabel("Username:");
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userField = new JTextField(10);
        userField.setMaximumSize(new Dimension(250, 200));
        box.add(userLabel);
        box.add(userField);
        box.add(Box.createVerticalStrut(25));
        JLabel passLabel = new JLabel("Password:");
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passField = new JPasswordField(10);
        passField.setMaximumSize(new Dimension(250, 200));
        box.add(passLabel);
        box.add(passField);
        box.add(Box.createVerticalStrut(25));
        continueButton = new JButton("Login");
        continueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                String password = charToString(passField.getPassword());
                Client client = getClient();
                if (loginMode) {
                    if (client.login(username, password)) {
                        getGUI().setScreen(new HomeScreen());
                    } else {
                        setButtonLabels();
                    }
                } else {
                    client.createUser(username, password);
                    //require login after
                    loginMode = !loginMode;
                    setButtonLabels();
                }
            }
        });
        switchButton = new JButton("To Create Account");
        switchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginMode = !loginMode;
                setButtonLabels();
            }
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(continueButton);
        buttonPanel.add(switchButton);
        box.add(buttonPanel);

        Border b = BorderFactory.createLineBorder(Color.BLACK, 2, true);
        box.setPreferredSize(new Dimension(270, 380));
        box.setMaximumSize(new Dimension(270, 380));
        box.setBorder(b);

        box.add(Box.createVerticalStrut(100));
        this.add(box);
        this.add(Box.createVerticalGlue());
    }

    private void setButtonLabels() {
        if (loginMode) {
            title.setText("Login");
            continueButton.setText("Login");
            switchButton.setText("To Create Account");
        } else {
            title.setText("Create New Account");
            continueButton.setText("Create");
            switchButton.setText("To Login");
        }
        userField.setText("");
        passField.setText("");
    }

    private String charToString(char[] c) {
        String ans = "";
        for (int i = 0; i < c.length; i++) {
            ans += c[i];
        }
        return ans;
    }
}
