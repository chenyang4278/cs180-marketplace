package packet.factory;

import packet.IPacketFactory;
import packet.Packet;

public class ErrorPacketFactory implements IPacketFactory {
    private String msg;

    public ErrorPacketFactory(String msg) {
        this.msg = msg;
    }

    @Override
    public Packet create() {
        Packet packet = new Packet();

        packet.setBody(msg);
        packet.addHeader("Status", "ERR");

        return packet;
    }
}
