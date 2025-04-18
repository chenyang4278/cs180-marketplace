package server.handlers;

import data.Listing;
import data.User;
import database.DatabaseWrapper;
import database.DatabaseWriteException;
import database.RowNotFoundException;
import packet.Packet;
import server.PacketHandler;
import packet.response.ErrorPacket;
import packet.response.ObjectPacket;

/**
 * EditListingHandler
 * <p>
 * Handles editing a listing from attribute
 *
 * @author Karma Luitel
 * @version 4/13/25
 */
public class EditListingHandler extends PacketHandler implements IEditListingHandler {
    public EditListingHandler() { super("/listing/edit"); }

    /* For example, an attribute would be "username", an attribute val would be "karma"
     * Expected PacketHeaders:
     *
     * listingId
     * attribute - arg in index 0
     * attributeVal  - arg in index 0
     */
    @Override
    public Packet handle(Packet packet, String[] args) {
        User user = packet.getUser();
        if (user == null) {
            return new ErrorPacket("Not logged in");
        }
        String[] data = packet.getHeaderValues("listingId", "attribute", "attributeVal");
        if (data == null) {
            return new ErrorPacket("Invalid packet headers!");
        }

        String sid = data[0];
        String attribute = data[1];
        String attributeVal = data[2];

        if (attribute.equals("price")) {
            double dbPrice;
            try {
                dbPrice = Double.parseDouble(attributeVal);
                if (dbPrice < 0) {
                    return new ErrorPacket("Invalid listing price!");
                }
            } catch (NumberFormatException e) {
                return new ErrorPacket("Invalid listing price!");
            }
        }

        int id = 0;
        try {
            id = Integer.parseInt(sid);
            try {
                db.setById(Listing.class, id, attribute, attributeVal);
                try {
                    return new ObjectPacket<Listing>(db.getById(Listing.class, id));
                } catch (RowNotFoundException e) {
                    return new ErrorPacket("Listing not found!");
                }
            } catch (DatabaseWriteException e) {
                return new ErrorPacket(e.getMessage());
            }
        } catch (NumberFormatException e) {
            return new ErrorPacket("Invalid id!");
        }
    }
}