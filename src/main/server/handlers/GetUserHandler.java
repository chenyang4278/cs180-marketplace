package server.handlers;

import database.RowNotFoundException;
import database.User;
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
public class GetUserHandler extends PacketHandler {
    public GetUserHandler() {
        super("/users/:id");
    }

    @Override
    public Packet handle(Packet packet, String[] args) {
        try {
            User user = db.getByColumn(User.class, "id", args[0]);
            return new ObjectPacket<>(user);
        } catch (RowNotFoundException ignored) {
            return new ErrorPacket("User not found");
        }
    }
}
