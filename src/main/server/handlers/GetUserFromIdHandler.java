package server.handlers;

import database.DatabaseWrapper;
import database.RowNotFoundException;
import data.User;
import packet.Packet;
import server.PacketHandler;
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
        User user = getSessionUser(packet);
        if (user == null) {
            return new ErrorPacket("You are not logged in!");
        }

        try {
            User requestedUser = db.getById(User.class, Integer.parseInt(args[0]));
            requestedUser.setPassword(null);
            return new ObjectPacket<>(requestedUser);
        } catch (RowNotFoundException ignored) {
            return new ErrorPacket("User not found");
        } catch (NumberFormatException ignored) {
            return new ErrorPacket("Invalid user id");
        }
    }
}
