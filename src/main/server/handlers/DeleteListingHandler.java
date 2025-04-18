package server.handlers;

import data.User;
import database.DatabaseWrapper;
import database.DatabaseWriteException;
import data.Listing;
import database.RowNotFoundException;
import packet.Packet;
import server.PacketHandler;
import packet.response.ErrorPacket;
import packet.response.SuccessPacket;

/**
 * DeleteListingHandler
 * <p>
 * Handles deleting listings.
 *
 * @author Karma Luitel
 * @version 4/13/25
 */
public class DeleteListingHandler extends PacketHandler implements IDeleteListingHandler {
    public DeleteListingHandler() {
        super("/listing/delete");
    }

    /*
     * Expected PacketHeaders:
     * listingId - arg in index 0
     */
    @Override
    public Packet handle(Packet packet, String[] args) {
        User user = packet.getUser();
        if (user == null) {
            return new ErrorPacket("Not logged in");
        }
        String[] data = packet.getHeaderValues("listingId");
        if (data == null) {
            return new ErrorPacket("Invalid packet headers!");
        }
        String sid = data[0];

        int id = 0;
        try {
            id = Integer.parseInt(sid);
            try {
                Listing l = db.getById(Listing.class, id);
                try {
                    db.delete(l);
                    return new SuccessPacket();
                } catch (DatabaseWriteException e) {
                    return new ErrorPacket("Database delete failure!");
                }
            } catch (RowNotFoundException ignored) {
                return new ErrorPacket("Listing does not exist!");
            }
        } catch (NumberFormatException e) {
            return new ErrorPacket("Invalid listing id!");
        }


    }
}