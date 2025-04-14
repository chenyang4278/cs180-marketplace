package server.handlers;

import data.Listing;
import data.User;
import database.*;
import packet.Packet;
import packet.PacketHandler;
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
        super("/buylisting/");
    }

    /*
     * Expected PacketHeaders:
     * buyingId - who is buying
     * listingId - which listing is being bought
     *
     * Will return a new user object with updated data.
     */

    @Override
    public Packet handle(Packet packet, String[] args) {
        String bsid = packet.getHeader("buyingId").getValues().get(0);
        String lsid = packet.getHeader("listingId").getValues().get(0);
        int bid = 0;
        int lid = 0;
        try {
            bid = Integer.parseInt(bsid);
            lid = Integer.parseInt(lsid);
            try {
                User u = db.getById(User.class, bid);
                Listing l = db.getById(Listing.class, lid);
                User s = db.getById(User.class, l.getSellerId());
                if (l.isSold()) {
                    return new ErrorPacket("Item already has already been sold!");
                }
                if (u.getBalance() < l.getPrice() + EPSILON) {
                    return new ErrorPacket("User does not have enough balance to buy this item!");
                }
                u.setBalance(u.getBalance() - l.getPrice());
                s.setBalance(s.getBalance() + l.getPrice());
                l.setSold(true);
                try {
                    DatabaseWrapper.get().save(u);
                    DatabaseWrapper.get().save(s);
                    DatabaseWrapper.get().save(l);
                    return new ObjectPacket<User>(u);
                } catch (DatabaseWriteException e) {
                    return new ErrorPacket("Database update error!");
                }
            } catch (RowNotFoundException e) {
                return new ErrorPacket("Could not find user or listing!");
            }
        } catch (NumberFormatException e) {
            return new ErrorPacket("Invalid ids!");
        }
    }
}