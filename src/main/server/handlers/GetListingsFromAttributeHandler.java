package server.handlers;

import data.Listing;
import data.User;
import database.DatabaseWrapper;
import packet.Packet;
import server.PacketHandler;
import packet.response.ErrorPacket;
import packet.response.ObjectListPacket;

import java.util.ArrayList;

/**
 * GetListingsFromAttributeHandler
 * <p>
 * Handles getting listings from a provided attribute.
 *
 * @author Karma Luitel
 * @version 4/13/25
 */
public class GetListingsFromAttributeHandler extends PacketHandler implements IGetListingsFromAttributeHandler {
    public GetListingsFromAttributeHandler() {
        super("/listings/attribute");
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

        String[] data = packet.getHeaderValues("attribute", "attributeVal");
        if (data == null) {
            return new ErrorPacket("Invalid packet headers!");
        }

        String attribute = data[0];
        String attributeVal = data[1];

        ArrayList<Listing> listings = (ArrayList<Listing>) db.filterByColumn(Listing.class,
            attribute, attributeVal, true);
        return new ObjectListPacket<Listing>(listings);
    }
}