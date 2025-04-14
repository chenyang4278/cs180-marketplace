package server.handlers;

import packet.Packet;

/**
 * IGetUserFromIdHandler
 *
 * @author Karma Luitel
 * @version 4/14/25
 */
public interface IGetUserFromIdHandler {
    Packet handle(Packet packet, String[] args);
}
