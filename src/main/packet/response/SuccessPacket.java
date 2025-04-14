package packet.response;

import packet.Packet;

import java.io.Serializable;

/**
 * SuccessPacket
 * <p>
 * Extends Packet with a default 'Status' header indicating
 * the packet is a successful response.
 *
 * @author Ayden Cline
 * @version 4/12/25
 */
public class SuccessPacket extends Packet implements Serializable, ISuccessPacket {
    public SuccessPacket() {
        addHeader("Status", "OK");
    }
}
