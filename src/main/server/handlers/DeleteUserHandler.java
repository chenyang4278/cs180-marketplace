package server.handlers;

import database.DatabaseWrapper;
import data.User;
import database.DatabaseWriteException;
import database.RowNotFoundException;
import packet.Packet;
import server.PacketHandler;
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
        super("/users/:id/delete");
    }

    @Override
    public Packet handle(Packet packet, String[] args) {
        User user = authenticate(packet);
        if (user == null) {
            return new ErrorPacket("Not logged in");
        }

        try {
            User userToDelete = db.getById(User.class, Integer.parseInt(args[0]));
            // may allow admins to delete in the future
            if (userToDelete.getId() != user.getId()) {
                return new ErrorPacket("No permission to delete another user");
            }

            db.delete(userToDelete);
            return new SuccessPacket();
        } catch (DatabaseWriteException e) {
            return new ErrorPacket("Database delete failure!");
        } catch (RowNotFoundException ignored) {
            return new ErrorPacket("User does not exist!");
        } catch (NumberFormatException e) {
            return new ErrorPacket("Invalid id!");
        }
    }
}