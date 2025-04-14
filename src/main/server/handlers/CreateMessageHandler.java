package server.handlers;

import data.Message;
import database.*;
import packet.Packet;
import packet.PacketHandler;
import packet.response.ErrorPacket;
import packet.response.ObjectPacket;

/**
 * CreateMessageHandler
 * <p>
 * Handles creating messages.
 *
 * @author Karma Luitel
 * @version 4/13/25
 */
public class CreateMessageHandler extends PacketHandler implements ICreateMessageHandler {
    public CreateMessageHandler() {
        super("/messagecreate/");
    }

    /*
     * Expected PacketHeaders:
     * senderId
     * recieverId
     * message
     */
    @Override
    public Packet handle(Packet packet, String[] args) {
        String ssid = packet.getHeader("senderId").getValues().get(0);
        String rsid = packet.getHeader("recieverId").getValues().get(0);
        String message = packet.getHeader("message").getValues().get(0);
        int sid = 0;
        int rid = 0;
        try {
            sid = Integer.parseInt(ssid);
            rid = Integer.parseInt(rsid);
            if (sid == rid) {
                return new ErrorPacket("You cannot message yourself!");
            }
            Message m = new Message(sid, rid, message);
            try {
                DatabaseWrapper.get().save(m);
                return new ObjectPacket<Message>(m);
            } catch (DatabaseWriteException e) {
                return new ErrorPacket("Database faliure in creating message");
            }
        } catch (NumberFormatException e) {
            return new ErrorPacket("Invalid ids!");
        }
    }
}