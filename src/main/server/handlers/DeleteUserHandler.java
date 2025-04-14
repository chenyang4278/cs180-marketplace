package server.handlers;

import database.DatabaseWrapper;
import database.User;
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
        super("/userdelete/");
    }

    /*
     * Expected PacketHeaders:
     * userId - arg in index 0
     */
    @Override
    public Packet handle(Packet packet, String[] args) {
        String sid = packet.getHeader("userId").getValues().get(0);
        int id = 0;
        try {
            id = Integer.parseInt(sid);
            try {
                User u = db.getById(User.class, id);
                try {
                    DatabaseWrapper.get().delete(u);
                    return new SuccessPacket();
                } catch (DatabaseWriteException e) {
                    return new ErrorPacket("Database delete faliure!");
                }
            } catch (RowNotFoundException ignored) {
                return new ErrorPacket("User does not exist!");
            }
        } catch (NumberFormatException e) {
            return new ErrorPacket("Invalid id!");
        }


    }
}