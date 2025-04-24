package client;

import client.screens.Screen;

public interface IClientGUI {
    void setScreen(Screen screen);
    void goBackScreen();
    boolean canGoBack();
}
