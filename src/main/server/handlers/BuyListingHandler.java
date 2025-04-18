package server.handlers;

import data.Listing;
import data.User;
import database.*;
import packet.Packet;
import server.PacketHandler;
import packet.response.ErrorPacket;
import packet.response.ObjectPacket;

/**
 * BuyHandler
 * <p>
 * Handles buying listings.
 *
 * @author Karma Luitel
 * @version 4/13/25
 */
public class BuyListingHandler extends PacketHandler implements IBuyListingHandler{

    final double EPSILON = 1e-9;

    public BuyListingHandler() {
        super("listings/:id/buy");
    }

    /*
     * Will return a new user object with updated data.
     */
    @Override
    public Packet handle(Packet packet, String[] args) {
        User user = getSessionUser(packet);
        if (user == null) {
            return new ErrorPacket("Not logged in");
        }

        try {
            Listing listing = db.getById(Listing.class, Integer.parseInt(args[0]));

            if (listing.isSold()) {
                return new ErrorPacket("Item already has already been sold!");
            }
          
            if (user.getBalance() < listing.getPrice() + EPSILON) {
                return new ErrorPacket("User does not have enough balance to buy this item!");
            }

            user.setBalance(user.getBalance() - listing.getPrice());
            db.save(user);

            User seller = db.getById(User.class, listing.getSellerId());
            seller.setBalance(seller.getBalance() + listing.getPrice());
            db.save(seller);

            listing.setSold(true);
            db.save(listing);

            return new ObjectPacket<User>(user);
        } catch (DatabaseWriteException e) {
            return new ErrorPacket("Database update error!");
        } catch (RowNotFoundException e) {
            return new ErrorPacket("Could not find user or listing!");
        } catch (NumberFormatException e) {
            return new ErrorPacket("Invalid ids!");
        }
    }
}