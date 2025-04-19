package server.handlers;

import packet.Packet;

/**
 * ICreateListingHandler
 *
 * @author Karma Luitel
 * @version 4/14/25
 */
public interface ICreateListingHandler {
    Packet handle(Packet packet, String[] args);
}
