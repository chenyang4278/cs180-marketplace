package client.screens;

import client.Client;
import client.ClientGUI;
import client.Program;

import javax.swing.*;

/**
 * Screen
 *
 * Extendable base class for webpages in our GUI
 *
 * @author Ayden Cline
 * @version 4/25/25
 */
public abstract class Screen extends JPanel implements IScreen {
    protected ClientGUI getGUI() {
        return Program.getGUI();
    }

    protected Client getClient() {
        return Program.getClient();
    }
}
