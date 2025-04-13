package server.handlers;

import database.DatabaseWriteException;
import database.Listing;
import database.RowNotFoundException;
import database.User;
import packet.Packet;
import packet.PacketHandler;
import packet.response.ErrorPacket;
import packet.response.ObjectPacket;

public class CreateListingHandler extends PacketHandler {
    public CreateListingHandler() {
        super("/listingcreate/");
    }

    /*
     * Expected PacketHeaders:
     * username - arg in index 0
     * title - arg in index 0
     * description - arg in index 0
     * price - arg in index 0
     * image - arg in index 0
     */
    @Override
    public Packet handle(Packet packet, String[] args) {

        String username = packet.getHeader("username").getValues().get(0);
        String title = packet.getHeader("title").getValues().get(0);
        String description = packet.getHeader("description").getValues().get(0);
        String price = packet.getHeader("price").getValues().get(0);
        String image = packet.getHeader("image").getValues().get(0);

        double dbPrice = 0;
        try {
            dbPrice = Double.parseDouble(price);
            if (dbPrice < 0) {
                return new ErrorPacket("Invalid listing price!");
            }
        } catch (NumberFormatException e) {
            return new ErrorPacket("Invalid listing price!");
        }
        try {
            db.getByColumn(Listing.class, "title", title);
            return new ErrorPacket("Listing already exists");
        } catch (RowNotFoundException ignored) {
            try {
                User u = db.getByColumn(User.class, "username", username);
                Listing l = new Listing(u.getId(), username, title, description, dbPrice, image, false);
                try {
                    l.save();
                    return new ObjectPacket<Listing>(l);
                } catch (DatabaseWriteException e) {
                    return new ErrorPacket("Database faliure in creating listing");
                }
            } catch (RowNotFoundException e) {
                return new ErrorPacket("Database faliure in reading user");
            }
        }
    }
}