package server.handlers;

import packet.Packet;

/**
 * IEditListingHandler
 *
 * @author Karma Luitel
 * @version 4/15/25
 */
public interface IEditListingHandler {
    Packet handle(Packet packet, String[] args);
}
