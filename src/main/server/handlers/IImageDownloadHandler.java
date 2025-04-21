package server.handlers;

import packet.Packet;

/**
 * IImageDownloadHandler
 *
 * @author Ayden Cline
 * @version 4/19/25
 */
public interface IImageDownloadHandler {
    Packet handle(Packet packet, String[] args);
}
