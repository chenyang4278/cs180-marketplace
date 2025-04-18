package server.handlers;

import data.Listing;
import data.User;
import database.*;
import packet.Packet;
import server.PacketHandler;
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
        super("/listings/create");
    }

    /*
     * Expected PacketHeaders:
     * title
     * description
     * price
     * image
     */
    @Override
    public Packet handle(Packet packet, String[] args) {
        User user = authenticate(packet);
        if (user == null) {
            return new ErrorPacket("Not logged in");
        }

        String[] data = packet.getHeaderValues("title", "description", "price", "image");
        if (data == null) {
            return new ErrorPacket("Invalid data");
        }

        String title = data[0];
        String description = data[1];
        String price = data[2];
        String image = data[3];

        double dbPrice;
        try {
            dbPrice = Double.parseDouble(price);
            if (dbPrice < 0) {
                return new ErrorPacket("Invalid listing price!");
            }
        } catch (NumberFormatException e) {
            return new ErrorPacket("Invalid listing price!");
        }

        try {
            Listing listing = new Listing(user.getId(), user.getUsername(), title, description, dbPrice, image, false);
            db.save(listing);
            return new ObjectPacket<Listing>(listing);
        } catch (DatabaseWriteException ignored) {
            return new ErrorPacket("Database failure in creating listing");
        }
    }
}