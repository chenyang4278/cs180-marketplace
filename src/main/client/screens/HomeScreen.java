package client.screens;

import javax.swing.*;

public class HomeScreen extends Screen {
    public HomeScreen() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        for (int i = 1; i <= 50; i++) {
            JLabel label = new JLabel("Label " + i);
            add(label);
        }
    }
}
