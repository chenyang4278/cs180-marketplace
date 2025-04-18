package server.handlers;

import data.User;
import database.DatabaseWrapper;
import database.DatabaseWriteException;
import data.Listing;
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
        super("/listings/:id/delete");
    }

    @Override
    public Packet handle(Packet packet, String[] args) {
        User user = packet.getUser();
        if (user == null) {
            return new ErrorPacket("Not logged in");
        }

        try {
            Listing listing = db.getById(Listing.class, Integer.parseInt(args[0]));
            if (listing.getSellerId() != user.getId()) {
                return new ErrorPacket("Cannot delete another user's listing");
            }

            listing.delete();
            return new SuccessPacket();
        } catch (DatabaseWriteException e) {
            return new ErrorPacket("Database delete failure!");
        } catch (RowNotFoundException ignored) {
            return new ErrorPacket("Listing does not exist!");
        } catch (NumberFormatException e) {
            return new ErrorPacket("Invalid listing id!");
        }
    }
}