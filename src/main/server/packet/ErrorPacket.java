package server.packet;

public class ErrorPacket extends Packet {
    public ErrorPacket(String message) {
        super(message);

        addHeader("Status", "ERR");
    }

    public String getMessage() {
        return getBody();
    }
}
