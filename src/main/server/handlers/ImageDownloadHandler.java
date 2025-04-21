package server.handlers;

import packet.Packet;
import packet.response.ErrorPacket;
import packet.response.SuccessPacket;
import server.PacketHandler;

import java.io.File;

public class ImageDownloadHandler extends PacketHandler implements IImageUploadHandler {
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
