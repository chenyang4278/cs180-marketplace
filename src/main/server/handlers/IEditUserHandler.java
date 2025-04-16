package server.handlers;

import packet.Packet;

/**
 * IEditUserHandler
 *
 * @author Karma Luitel
 * @version 4/15/25
 */
public interface IEditUserHandler {
    Packet handle(Packet packet, String[] args);
}
