package client;

import client.screens.Screen;

/**
 * IClientGUI
 *
 * @author Ayden Cline
 * @version 4/20/25
 */
public interface IClientGUI {
    void setScreen(Screen screen);
    void goBackScreen();
    boolean canGoBack();
}
