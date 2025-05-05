package client;

import client.screens.Screen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Stack;

/**
 * ClientGUI
 *
 * Serves various windows for our client GUI.
 *
 * @author Ayden Cline
 * @version 4/20/25
 */
public class ClientGUI implements IClientGUI {
    private final JFrame frame;
    private final JScrollPane scrollPane;
    private final Header header;
    private final Stack<Screen> screenHistory = new Stack<>();

    public ClientGUI() {
        frame = new JFrame("Shop Client");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setMinimumSize(new Dimension(600, 400));
        frame.setSize(800, 600);

        scrollPane = new JScrollPane();
        JScrollBar jsb = scrollPane.getVerticalScrollBar();
        jsb.setUnitIncrement(16);
        header = new Header();
        frame.add(header, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //Deleting tmp directory
                File dir = new File("tmp/");
                for (File f : dir.listFiles()) {
                    f.delete();
                }
                dir.delete();
            }
        });
        frame.setVisible(true);


    }

    public void setScreen(Screen screen) {
        screen.setVisible(true);
        screenHistory.push(screen);
        scrollPane.setViewportView(screen);
        header.refresh();
    }

    public void goBackScreen() {
        if (screenHistory.size() == 1) {
            return;
        }

        screenHistory.pop();
        JPanel newScreen = screenHistory.peek();
        newScreen.setVisible(true);  // should be true already, but just to be sure
        scrollPane.setViewportView(newScreen);
        header.refresh();
    }

    public boolean canGoBack() {
        return screenHistory.size() > 1;
    }
}
