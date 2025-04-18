package server.handlers;

import data.Message;
import data.User;
import database.DatabaseWrapper;
import packet.Packet;
import server.PacketHandler;
import packet.response.ErrorPacket;
import packet.response.ObjectListPacket;

import java.util.ArrayList;

/**
 * GetMessagesBetweenUsersHandler
 * <p>
 * Handles getting messages between two users.
 *
 * @author Karma Luitel
 * @version 4/13/25
 */
public class GetMessagesBetweenUsersHandler extends PacketHandler implements IGetMessagesBetweenUsersHandler {
    public GetMessagesBetweenUsersHandler() { super("/get/messages"); }

    /*
     * Expected PacketHeaders:
     * senderId - arg in index 0
     * receiverId  - arg in index 0
     */
    @Override
    public Packet handle(Packet packet, String[] args) {
        User user = packet.getUser();
        if (user == null) {
            return new ErrorPacket("Not logged in");
        }
        String[] data = packet.getHeaderValues("senderId", "receiverId");
        if (data == null) {
            return new ErrorPacket("Invalid packet headers!");
        }

        String ssid = data[0];
        String rsid = data[1];

        int sid = 0;
        int rid = 0;
        try {
            sid = Integer.parseInt(ssid);
            rid = Integer.parseInt(rsid);
            if (sid == rid) {
                return new ErrorPacket("You cannot message yourself!");
            }
            ArrayList<Message> messagesI = (ArrayList<Message>) db.filterByColumn(Message.class, "senderId", "" + sid);
            ArrayList<Message> messagesF = new ArrayList<>();
            for (Message m : messagesI) {
                if (m.getReceiverId() == rid) {
                    messagesF.add(m);
                }
            }
            return new ObjectListPacket<Message>(messagesF);
        } catch (NumberFormatException e) {
            return new ErrorPacket("Invalid ids!");
        }
    }
}