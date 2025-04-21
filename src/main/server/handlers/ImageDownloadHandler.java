package server.handlers;

import packet.Packet;
import packet.response.ErrorPacket;
import packet.response.SuccessPacket;
import server.PacketHandler;

import java.io.File;

/**
 * ImageDownloadHandler
 * <p>
 * Handles sending files to the client.
 *
 * @author Ayden Cline
 * @version 4/20/2025
 */
public class ImageDownloadHandler extends PacketHandler implements IImageDownloadHandler {
    public ImageDownloadHandler() {
        super("/static/:hash");
    }

    @Override
    public Packet handle(Packet packet, String[] args) {
        File file = new File("static/" + args[0]);
        if (!file.isFile()) {
            return new ErrorPacket("File not found");
        }

        SuccessPacket resp = new SuccessPacket();
        resp.addHeader("Download-Hash", args[0]);
        return resp;
    }
}
