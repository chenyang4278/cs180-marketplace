package client;

import client.screens.Screen;

import javax.swing.*;
import java.awt.*;
import java.util.Stack;

public class ClientGUI {
    private final JFrame frame;
    private final JScrollPane scrollPane;
    private final Stack<Screen> screenHistory = new Stack<>();

    public ClientGUI() {
        frame = new JFrame("Title");  // todo
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setMinimumSize(new Dimension(600, 400));
        frame.setSize(800, 600);

        scrollPane = new JScrollPane();
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        frame.add(new Header(), BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public void setScreen(Screen screen) {
        screen.setVisible(true);
        screenHistory.push(screen);
        scrollPane.setViewportView(screen);
    }

    public void goBackScreen() {
        if (screenHistory.size() == 1) {
            return;
        }

        screenHistory.pop();
        JPanel newScreen = screenHistory.peek();
        newScreen.setVisible(true);  // should be true already, but just to be sure
        scrollPane.add(newScreen);
    }

    public boolean canGoBack() {
        return screenHistory.size() > 1;
    }
}
