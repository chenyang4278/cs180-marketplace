package packet.factory;

import packet.IPacketFactory;
import packet.Packet;

public class SuccessPacketFactory implements IPacketFactory {
    private String path;

    public SuccessPacketFactory() {
        this.path = "";
    }

    public SuccessPacketFactory(String path) {
        this.path = path;
    }

    @Override
    public Packet create() {
        Packet packet = new Packet();

        packet.setPath(path);
        packet.addHeader("Status", "OK");

        return packet;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
