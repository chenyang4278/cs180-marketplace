package server.handlers;

import packet.Packet;

/**
 * IImageUploaderHandler
 *
 * @author Ayden Cline
 * @version 4/19/25
 */
public interface IImageUploadHandler {
    Packet handle(Packet packet, String[] args);
}
