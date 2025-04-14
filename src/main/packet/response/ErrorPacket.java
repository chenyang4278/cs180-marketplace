package packet.response;

import packet.Packet;

import java.io.Serializable;

/**
 * ErrorPacket
 * <p>
 * Extends Packet with a default 'Status' header indicating an error
 * and a message attribute.
 *
 * @author Ayden Cline
 * @version 4/12/25
 */
public class ErrorPacket extends Packet implements Serializable, IErrorPacket {
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
