package server.handlers;

import data.Message;
import data.User;
import database.*;
import packet.Packet;
import server.PacketHandler;
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
        super("/messages/create");
    }

    /*
     * Expected PacketHeaders:
     * receiverId
     * message
     */
    @Override
    public Packet handle(Packet packet, String[] args) {
        User user = authenticate(packet);
        if (user == null) {
            return new ErrorPacket("Not logged in");
        }

        String[] data = packet.getHeaderValues("receiverId", "message");
        if (data == null) {
            return new ErrorPacket("Invalid data");
        }

        try {
            int receiverId = Integer.parseInt(data[0]);
            if (user.getId() == receiverId) {
                return new ErrorPacket("You cannot message yourself!");
            }
            Message msg = new Message(user.getId(), receiverId, data[1]);
            db.save(msg);

            return new ObjectPacket<Message>(msg);
        } catch (DatabaseWriteException e) {
            return new ErrorPacket("Database failure in creating message");
        } catch (NumberFormatException e) {
            return new ErrorPacket("Invalid ids!");
        }
    }
}