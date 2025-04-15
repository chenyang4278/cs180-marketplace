package server.handlers;

import data.Message;
import data.User;
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
        super("/message/create");
    }

    /*
     * Expected PacketHeaders:
     * senderId
     * recieverId
     * message
     */
    @Override
    public Packet handle(Packet packet, String[] args) {

        User user = packet.getUser();
        if (user == null) {
            return new ErrorPacket("Not logged in");
        }

        String[] data = packet.getHeaderValues("senderId", "recieverId", "message");
        if (data == null) {
            return new ErrorPacket("Invalid packet headers!");
        }

        String ssid = data[0];
        String rsid = data[1];
        String message = data[2];

        int sid = 0;
        int rid = 0;
        try {
            sid = Integer.parseInt(ssid);
            rid = Integer.parseInt(rsid);
            if (sid == rid) {
                return new ErrorPacket("You cannot message yourself!");
            }
            if (message.trim().isEmpty()) {
                return new ErrorPacket("You cannot have an empty message!");
            }
            Message m = new Message(sid, rid, message);
            try {
                DatabaseWrapper.get().save(m);
                return new ObjectPacket<Message>(m);
            } catch (DatabaseWriteException e) {
                return new ErrorPacket("Database failure in creating message");
            }
        } catch (NumberFormatException e) {
            return new ErrorPacket("Invalid ids!");
        }
    }
}