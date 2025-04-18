package server;

import data.User;
import packet.Packet;

/**
 * IPacketHandler
 *
 * @author Ayden Cline
 * @version 4/12/25
 */
public interface IPacketHandler {
    Packet handle(Packet packet, String[] args);
    User authenticate(Packet packet);
    String[] match(String matchingPath);
}
