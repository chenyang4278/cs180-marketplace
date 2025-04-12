package packet.response;

import packet.Packet;

import java.io.Serializable;

public class SuccessPacket extends Packet implements Serializable {
    public SuccessPacket() {
        addHeader("Status", "OK");
    }
}
