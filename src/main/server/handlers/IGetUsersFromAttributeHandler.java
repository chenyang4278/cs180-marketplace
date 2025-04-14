package server.handlers;

import packet.Packet;

/**
 * IGetUsersFromAttributeHandler
 *
 * @author Karma Luitel
 * @version 4/14/25
 */
public interface IGetUsersFromAttributeHandler {
    Packet handle(Packet packet, String[] args);
}
