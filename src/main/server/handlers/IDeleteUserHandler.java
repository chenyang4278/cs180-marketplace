package server.handlers;

import packet.Packet;

/**
 * IDeleteUserHandler
 *
 * @author Karma Luitel
 * @version 4/14/25
 */
public interface IDeleteUserHandler {
    Packet handle(Packet packet, String[] args);
}
