package server.handlers;

import data.Message;
import data.User;
import database.DatabaseWrapper;
import packet.Packet;
import server.PacketHandler;
import packet.response.ErrorPacket;
import packet.response.ObjectListPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * GetMessagesBetweenUsersHandler
 * <p>
 * Handles getting messages between two users.
 *
 * @author Karma Luitel
 * @version 4/13/25
 */
public class GetMessagesBetweenUsersHandler extends PacketHandler implements IGetMessagesBetweenUsersHandler {
    public GetMessagesBetweenUsersHandler() {
        super("/messages");
    }

    /*
     * Expected PacketHeaders:
     * otherUserId  - arg in index 0
     */
    @Override
    public Packet handle(Packet packet, String[] args) {
        User user = getSessionUser(packet);
        if (user == null) {
            return new ErrorPacket("Not logged in");
        }

        String[] data = packet.getHeaderValues("otherUserId");
        if (data == null) {
            return new ErrorPacket("Invalid packet headers!");
        }

        try {
            int otherUserId = Integer.parseInt(data[0]);
            if (otherUserId == user.getId()) {
                return new ErrorPacket("No messages between you and yourself");
            }

            List<Message> sentMessages = db.filterByColumn(
                    Message.class,
                    "senderId",
                    "" + user.getId()
            );
            List<Message> receivedMessages = db.filterByColumn(
                    Message.class,
                    "receiverId",
                    "" + user.getId()
            );

            // filter out messages between the two users
            // and return sorted by timestamp in order of recent to oldest
            ArrayList<Message> messages = (ArrayList<Message>) Stream.concat(
                            sentMessages.stream().filter((msg) -> msg.getReceiverId() == otherUserId),
                            receivedMessages.stream().filter((msg) -> msg.getSenderId() == otherUserId)
                    ).sorted((msg1, msg2) -> (int) (msg2.getTimestamp() - msg1.getTimestamp()))
                    .collect(Collectors.toList());

            return new ObjectListPacket<Message>(messages);
        } catch (NumberFormatException e) {
            return new ErrorPacket("Invalid ids!");
        }
    }
}