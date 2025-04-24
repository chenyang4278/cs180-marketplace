package client.screens;

import client.Client;
import client.ClientGUI;
import client.Program;

import javax.swing.*;

public abstract class Screen extends JPanel implements IScreen {
    protected ClientGUI getGUI() {
        return Program.getGUI();
    }

    protected Client getClient() {
        return Program.getClient();
    }
}
