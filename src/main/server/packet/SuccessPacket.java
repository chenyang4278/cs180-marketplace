package server.packet;

import java.util.List;

public class SuccessPacket extends Packet {
    public SuccessPacket(List<PacketHeader> headers, String body) {
        super("", headers, body);

        addHeader("Status", "OK");
    }
}
