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

/**
 * GetInboxUsersHandler
 * Gets a list of users in an users inbox.
 *
 * @author Ayden Cline
 * @version 4/25/25
 */
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
            return new ErrorPacket("You are not logged in!");
        }

        List<Message> messagesToMe = db.filterByColumn(
            Message.class,
            "receiverId",
            Integer.toString(user.getId()),
            false
        );
        List<Message> messagesFromMe = db.filterByColumn(
            Message.class,
            "senderId",
            Integer.toString(user.getId()),
            false
        );

        ArrayList<User> inboxUsers = new ArrayList<User>();
        extractUsers(user, inboxUsers, messagesToMe);
        extractUsers(user, inboxUsers, messagesFromMe);

        return new ObjectListPacket<User>(inboxUsers);
    }
}
