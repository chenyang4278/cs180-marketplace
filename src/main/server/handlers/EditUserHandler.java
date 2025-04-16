package server.handlers;

import data.User;
import database.DatabaseWrapper;
import database.DatabaseWriteException;
import database.RowNotFoundException;
import packet.Packet;
import packet.PacketHandler;
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
    public EditUserHandler() { super("/user/edit"); }

    /* For example, an attribute would be "username", an attribute val would be "karma"
     * Expected PacketHeaders:
     *
     * userId
     * attribute - arg in index 0
     * attributeVal  - arg in index 0
     */
    @Override
    public Packet handle(Packet packet, String[] args) {
        User user = packet.getUser();
        if (user == null) {
            return new ErrorPacket("Not logged in");
        }
        String[] data = packet.getHeaderValues("userId", "attribute", "attributeVal");
        if (data == null) {
            return new ErrorPacket("Invalid packet headers!");
        }

        String sid = data[0];
        String attribute = data[1];
        String attributeVal = data[2];

        if (attribute.equals("username")) {
            try {
                DatabaseWrapper.get().getByColumn(User.class, "username", attributeVal);
                return new ErrorPacket("Username already exists!");
            } catch (RowNotFoundException e) { e.getMessage(); }
        }
        int id = 0;
        try {
            id = Integer.parseInt(sid);
            try {
                DatabaseWrapper.get().setById(User.class, id, attribute, attributeVal);
                try {
                    return new ObjectPacket<User>(DatabaseWrapper.get().getById(User.class, id));
                } catch (RowNotFoundException e) {
                    return new ErrorPacket("User not found!");
                }
            } catch (DatabaseWriteException e) {
                return new ErrorPacket(e.getMessage());
            }
        } catch (NumberFormatException e) {
            return new ErrorPacket("Invalid id!");
        }
    }
}