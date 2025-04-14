package server.handlers;

import packet.Packet;

/**
 * ICreateUserHandler
 *
 * @author Karma Luitel
 * @version 4/14/25
 */
public interface ICreateUserHandler {
    Packet handle(Packet packet, String[] args);
}
