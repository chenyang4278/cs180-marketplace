package server.handlers;

import data.Message;
import data.User;
import database.RowNotFoundException;
import packet.Packet;
import packet.response.ErrorPacket;
import packet.response.ObjectListPacket;
import server.PacketHandler;

import java.util.ArrayList;
import java.util.List;

public class GetInboxUsersHandler extends PacketHandler implements IGetInboxUsersHandler {
    public GetInboxUsersHandler() {
        super("/messages/users");
    }

    private void extractUsers(User me, ArrayList<User> users, List<Message> messages) {
        for (Message message : messages) {
            int otherUserId = message.getSenderId() == me.getId() ?
                message.getReceiverId() : message.getSenderId();

            // check if user already added
            boolean userAdded = false;
            for (User u : users) {
                if (u.getId() == otherUserId) {
                    userAdded = true;
                    break;
                }
            }
            if (userAdded) {
                continue;
            }

            try {
                users.add(db.getById(User.class, otherUserId));
            } catch (RowNotFoundException ignored) {  // likely deleted user
                continue;
            }
        }
    }

    @Override
    public Packet handle(Packet packet, String[] args) {
        User user = getSessionUser(packet);
        if (user == null) {
            return new ErrorPacket("Not logged in");
        }

        List<Message> messagesToMe = db.filterByColumn(
            Message.class,
            "receiverId",
            Integer.toString(user.getId())
        );
        List<Message> messagesFromMe = db.filterByColumn(
            Message.class,
            "senderId",
            Integer.toString(user.getId())
        );

        ArrayList<User> inboxUsers = new ArrayList<User>();
        extractUsers(user, inboxUsers, messagesToMe);
        extractUsers(user, inboxUsers, messagesFromMe);

        return new ObjectListPacket<User>(inboxUsers);
    }
}
