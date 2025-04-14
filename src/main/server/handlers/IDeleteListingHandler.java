package server.handlers;

import packet.Packet;

/**
 * IDeleteListingHandler
 *
 * @author Karma Luitel
 * @version 4/14/25
 */
public interface IDeleteListingHandler {
    Packet handle(Packet packet, String[] args);
}
