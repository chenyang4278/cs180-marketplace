package server.handlers;

import packet.Packet;

/**
 * IGetListingsFromAttributeHandler
 *
 * @author Karma Luitel
 * @version 4/14/25
 */
public interface IGetListingsFromAttributeHandler {
    Packet handle(Packet packet, String[] args);
}
