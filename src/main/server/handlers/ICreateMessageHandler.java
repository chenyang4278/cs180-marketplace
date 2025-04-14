package server.handlers;

import packet.Packet;

/**
 * ICreateMessageHandler
 *
 * @author Karma Luitel
 * @version 4/14/25
 */
public interface ICreateMessageHandler {
    Packet handle(Packet packet, String[] args);
}
