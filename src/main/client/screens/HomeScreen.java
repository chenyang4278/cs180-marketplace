package client.screens;

import client.Styles;

import javax.swing.*;
import java.awt.*;

public class HomeScreen extends Screen implements IHomeScreen {
    public HomeScreen() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Shop Client");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(Styles.TITLE_FONT);
        JLabel description = new JLabel("A place for buying and selling anything");
        description.setFont(Styles.SUBTITLE_FONT);
        description.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(title);
        add(description);
    }
}
