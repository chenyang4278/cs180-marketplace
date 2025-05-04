package server.handlers;

import packet.Packet;

/**
 * IGetInboxUsersHandler
 *
 * @author Ayden Cline
 * @version 4/25/25
 */
public interface IGetInboxUsersHandler {
    Packet handle(Packet packet, String[] args);
}
