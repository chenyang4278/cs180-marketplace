package server.handlers;

import database.Message;
import database.RowNotFoundException;
import database.User;
import packet.Packet;
import packet.PacketHandler;
import packet.response.ErrorPacket;
import packet.response.ObjectListPacket;

import java.util.ArrayList;

public class GetMessagesBetweenUsersHandler extends PacketHandler {
    public GetMessagesBetweenUsersHandler() { super("/getmessages/"); }

    /*
     * Expected PacketHeaders:
     * senderId - arg in index 0
     * recieverId  - arg in index 0
     */
    @Override
    public Packet handle(Packet packet, String[] args) {
        String ssid = packet.getHeader("senderId").getValues().get(0);
        String rsid = packet.getHeader("recieverId").getValues().get(0);
        int sid = 0;
        int rid = 0;
        try {
            sid = Integer.parseInt(ssid);
            rid = Integer.parseInt(rsid);
            ArrayList<Message> messagesI = (ArrayList<Message>) db.filterByColumn(Message.class, "sender_id", "" + sid);
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