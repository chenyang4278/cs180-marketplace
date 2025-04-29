package server.handlers;

import data.User;
import packet.Packet;
import packet.response.ErrorPacket;
import packet.response.SuccessPacket;
import server.PacketHandler;

/**
 * ImageUploadHandler
 * <p>
 * Handles receiving files from the client.
 *
 * @author Ayden Cline
 * @version 4/19/2025
 */
public class ImageUploadHandler extends PacketHandler implements IImageUploadHandler {
    public ImageUploadHandler() {
        super("/upload");
    }

    @Override
    public Packet handle(Packet packet, String[] args) {
        User user = getSessionUser(packet);
        if (user == null) {
            return new ErrorPacket("You are not logged in!");
        }

        String[] data = packet.getHeaderValues("File-Hash");
        if (data == null) {
            return new ErrorPacket("No file provided!");
        }

        // signifies the packet body was part of an upload
        Packet resp = new SuccessPacket();
        resp.addHeader("File-Hash", data[0]);
        return resp;
    }
}
