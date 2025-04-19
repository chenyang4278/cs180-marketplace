package server.handlers;

import packet.Packet;

/**
 * IBuyListingHandler
 *
 * @author Karma Luitel
 * @version 4/14/25
 */
public interface IBuyListingHandler {
    Packet handle(Packet packet, String[] args);
}
