package server.handlers;

import data.User;
import database.DatabaseWrapper;
import packet.Packet;
import server.PacketHandler;
import packet.response.ErrorPacket;
import packet.response.ObjectListPacket;

import java.util.ArrayList;

/**
 * GetUsersFromAttributeHandler
 * <p>
 * Handles getting users from a provided attribute.
 *
 * @author Karma Luitel
 * @version 4/13/25
 */
public class GetUsersFromAttributeHandler extends PacketHandler implements IGetUsersFromAttributeHandler {
    public GetUsersFromAttributeHandler() {
        super("/users/attribute");
    }

    /* For example, an attribute would be "username", an attribute val would be "karma"
     * Expected PacketHeaders:
     * attribute - arg in index 0
     * attributeVal  - arg in index 0
     */
    @Override
    public Packet handle(Packet packet, String[] args) {
        User user = getSessionUser(packet);
        if (user == null) {
            return new ErrorPacket("You are not logged in!");
        }

        String[] data = packet.getHeaderValues("attribute", "attributeVal", "leniency");
        if (data == null) {
            return new ErrorPacket("Invalid packet headers!");
        }

        String attribute = data[0];
        String attributeVal = data[1];
        boolean leniency = data[2].equals("true");

        if (attribute.equals("password")) {
            return new ErrorPacket("You cannot obtain an user from a password!");
        }

        ArrayList<User> users = (ArrayList<User>) db.filterByColumn(User.class, attribute, attributeVal, leniency);
        for (User u : users) {
            u.setPassword(null);
        }
        return new ObjectListPacket<User>(users);
    }
}