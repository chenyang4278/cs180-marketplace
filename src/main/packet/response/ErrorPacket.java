package packet.response;

import packet.Packet;

import java.io.Serializable;

public class ErrorPacket extends Packet implements Serializable {
    private String message;

    public ErrorPacket(String message) {
        this.message = message;

        addHeader("Status", "ERR");
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
