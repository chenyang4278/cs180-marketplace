package server.handlers;

import database.DatabaseWrapper;
import database.DatabaseWriteException;
import database.Listing;
import database.RowNotFoundException;
import packet.Packet;
import packet.PacketHandler;
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
        super("/listingdelete/");
    }

    /*
     * Expected PacketHeaders:
     * listingId - arg in index 0
     */
    @Override
    public Packet handle(Packet packet, String[] args) {
        String sid = packet.getHeader("listingId").getValues().get(0);
        int id = 0;
        try {
            id = Integer.parseInt(sid);
            try {
                Listing l = db.getById(Listing.class, id);
                try {
                    DatabaseWrapper.get().delete(l);
                    return new SuccessPacket();
                } catch (DatabaseWriteException e) {
                    return new ErrorPacket("Database delete faliure!");
                }
            } catch (RowNotFoundException ignored) {
                return new ErrorPacket("Listing does not exist!");
            }
        } catch (NumberFormatException e) {
            return new ErrorPacket("Invalid listing id!");
        }


    }
}