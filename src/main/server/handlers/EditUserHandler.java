package server.handlers;

import data.User;
import database.DatabaseWrapper;
import database.DatabaseWriteException;
import database.RowNotFoundException;
import packet.Packet;
import server.PacketHandler;
import packet.response.ErrorPacket;
import packet.response.ObjectPacket;

/**
 * EditUserHandler
 * <p>
 * Handles editing a user from attribute
 *
 * @author Karma Luitel
 * @version 4/13/25
 */
public class EditUserHandler extends PacketHandler implements IEditUserHandler {
    public EditUserHandler() {
        super("/users/:id/edit");
    }

    /* For example, an attribute would be "username", an attribute val would be "karma"
     * Expected PacketHeaders:
     *
     * attribute - arg in index 0
     * attributeVal  - arg in index 0
     */
    @Override
    public Packet handle(Packet packet, String[] args) {
        User user = getSessionUser(packet);
        if (user == null) {
            return new ErrorPacket("You are not logged in!");
        }

        String[] data = packet.getHeaderValues("attribute", "attributeVal");
        if (data == null) {
            return new ErrorPacket("Invalid packet headers!");
        }

        String attribute = data[0];
        String attributeVal = data[1];

        if (attribute.equals("id")) {
            return new ErrorPacket("You cannot edit an id!");
        }

        if (attribute.equals("username")) {
            try {
                db.getByColumn(User.class, "username", attributeVal);
                return new ErrorPacket("Username already exists!");
            } catch (RowNotFoundException e) {
                e.getMessage();
            }
        }

        if (attribute.equals("password")) {
            attributeVal = HandlerUtil.hashPassword(attributeVal);
        }

        try {
            int id = Integer.parseInt(args[0]);
            User userToUpdate = db.getById(User.class, id);
            if (userToUpdate.getId() != user.getId()) {
                return new ErrorPacket("You don't have permission to edit another user!");
            }

            db.setById(User.class, id, attribute, attributeVal);

            User updatedUser = db.getById(User.class, id);
            updatedUser.setPassword(null);
            return new ObjectPacket<User>(updatedUser);
        } catch (RowNotFoundException e) {
            return new ErrorPacket("User not found!");
        } catch (DatabaseWriteException e) {
            return new ErrorPacket(e.getMessage());
        } catch (NumberFormatException e) {
            return new ErrorPacket("Invalid id!");
        }
    }
}