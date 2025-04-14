package server.handlers;

import packet.Packet;

/**
 * IGetMessagesBetweenUsersHandler
 *
 * @author Karma Luitel
 * @version 4/14/25
 */
public interface IGetMessagesBetweenUsersHandler {
    Packet handle(Packet packet, String[] args);
}
