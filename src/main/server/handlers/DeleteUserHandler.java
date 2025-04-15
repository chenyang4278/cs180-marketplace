package server.handlers;

import database.DatabaseWrapper;
import data.User;
import database.DatabaseWriteException;
import database.RowNotFoundException;
import packet.Packet;
import packet.PacketHandler;
import packet.response.ErrorPacket;
import packet.response.SuccessPacket;

/**
 * DeleteUserHandler
 * <p>
 * Handles deleting users.
 *
 * @author Karma Luitel
 * @version 4/13/25
 */
public class DeleteUserHandler extends PacketHandler implements IDeleteUserHandler {
    public DeleteUserHandler() {
        super("/user/delete");
    }

    /*
     * Expected PacketHeaders:
     * userId - arg in index 0
     */
    @Override
    public Packet handle(Packet packet, String[] args) {
        User user = packet.getUser();
        if (user == null) {
            return new ErrorPacket("Not logged in");
        }
        String[] data = packet.getHeaderValues("userId");
        if (data == null) {
            return new ErrorPacket("Invalid packet headers!");
        }
        String sid = data[0];

        int id = 0;
        try {
            id = Integer.parseInt(sid);
            try {
                User u = db.getById(User.class, id);
                try {
                    DatabaseWrapper.get().delete(u);
                    return new SuccessPacket();
                } catch (DatabaseWriteException e) {
                    return new ErrorPacket("Database delete failure!");
                }
            } catch (RowNotFoundException ignored) {
                return new ErrorPacket("User does not exist!");
            }
        } catch (NumberFormatException e) {
            return new ErrorPacket("Invalid id!");
        }


    }
}