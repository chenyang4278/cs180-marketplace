package server.handlers;

import data.Listing;
import data.User;
import database.*;
import packet.Packet;
import packet.PacketHandler;
import packet.response.ErrorPacket;
import packet.response.ObjectPacket;

/**
 * CreateListingHandler
 * <p>
 * Handles creating listings.
 *
 * @author Karma Luitel
 * @version 4/13/25
 */
public class CreateListingHandler extends PacketHandler implements ICreateListingHandler {
    public CreateListingHandler() {
        super("/listingcreate/");
    }

    /*
     * Expected PacketHeaders:
     * userId - arg in index 0
     * title - arg in index 0
     * description - arg in index 0
     * price - arg in index 0
     * image - arg in index 0
     */
    @Override
    public Packet handle(Packet packet, String[] args) {

        String userId = packet.getHeader("userId").getValues().get(0);
        String title = packet.getHeader("title").getValues().get(0);
        String description = packet.getHeader("description").getValues().get(0);
        String price = packet.getHeader("price").getValues().get(0);
        String image = packet.getHeader("image").getValues().get(0);

        double dbPrice = 0;
        int id = 0;
        try {
            dbPrice = Double.parseDouble(price);
            if (dbPrice < 0) {
                return new ErrorPacket("Invalid listing price!");
            }
        } catch (NumberFormatException e) {
            return new ErrorPacket("Invalid listing price!");
        }

        try {
            id = Integer.parseInt(userId);
        } catch (NumberFormatException e) {
            return new ErrorPacket("Invalid user id!");
        }

        try {
            User u = db.getById(User.class, id);
            Listing l = new Listing(id, u.getUsername(), title, description, dbPrice, image, false);
            try {
                DatabaseWrapper.get().save(l);
                return new ObjectPacket<Listing>(l);
            } catch (DatabaseWriteException e) {
                return new ErrorPacket("Database faliure in creating listing");
            }
        } catch (RowNotFoundException e) {
            return new ErrorPacket("Database faliure in reading user");
        }
    }
}