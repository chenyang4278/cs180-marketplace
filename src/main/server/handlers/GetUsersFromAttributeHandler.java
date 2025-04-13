package server.handlers;

import database.User;
import packet.Packet;
import packet.PacketHandler;
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
public class GetUsersFromAttributeHandler extends PacketHandler {
    public GetUsersFromAttributeHandler() {
        super("/usersattribute/");
    }

    /* For example, an attribute would be "username", an attribute val would be "karma"
     * Expected PacketHeaders:
     * attribute - arg in index 0
     * attributeval  - arg in index 0
     */
    @Override
    public Packet handle(Packet packet, String[] args) {
        String attribute = packet.getHeader("attribute").getValues().get(0);
        String attributeVal = packet.getHeader("attributeval").getValues().get(0);
        ArrayList<User> users = (ArrayList<User>) db.filterByColumn(User.class, attribute, attributeVal);
        return new ObjectListPacket<User>(users);
    }
}