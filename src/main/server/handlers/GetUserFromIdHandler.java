package server.handlers;

import database.RowNotFoundException;
import data.User;
import packet.Packet;
import packet.PacketHandler;
import packet.response.ErrorPacket;
import packet.response.ObjectPacket;

/**
 * GetUserHandler
 * <p>
 * Packet handler that returns a user given an id
 *
 * @author Ayden Cline
 * @version 4/12/25
 */
public class GetUserFromIdHandler extends PacketHandler implements IGetUserFromIdHandler {
    public GetUserFromIdHandler() {
        super("/users/:id");
    }

    @Override
    public Packet handle(Packet packet, String[] args) {
        User user = packet.getUser();
        if (user == null) {
            return new ErrorPacket("Not logged in");
        }

        try {
            User u = db.getByColumn(User.class, "id", args[0]);
            return new ObjectPacket<>(u);
        } catch (RowNotFoundException ignored) {
            return new ErrorPacket("User not found");
        }
    }
}
