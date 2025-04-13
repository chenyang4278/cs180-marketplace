package server.handlers;

import database.RowNotFoundException;
import database.User;
import packet.Packet;
import packet.PacketHandler;
import packet.response.ErrorPacket;
import packet.response.ObjectPacket;

/* Handlers that we will need:
 * createAccount (username, password) - creates an account in db, get user obj - DONE
 * login (username + password) - will retrieve a user object
 * createListing (price, name, ...) - creates a listing in db - DONE
 * deleteListing - delete listing from db
 * deleteAccount - delete an user account from db
 * getListings (based on listing title or associated user) - list of listing objects with attributes
 * getUsers (based on username/other attribute) - list of users
 * buy (balance + update listing) - will update user balance and listing in db, returns updated user obj?
 * sendMessage - will create a message between two users
 * getMessage (user1, user2) - will get messages between user1 and user 2
 *
 * May want to consider consolidating handlers in 1 or a few classes.
 */

/**
 * GetUserHandler
 * <p>
 * Packet handler that returns a user given an id
 *
 * @author Ayden Cline
 * @version 4/12/25
 */
public class GetUserFromIdHandler extends PacketHandler {
    public GetUserFromIdHandler() {
        super("/users/:id");
    }

    @Override
    public Packet handle(Packet packet, String[] args) {
        try {
            User user = db.getByColumn(User.class, "id", args[0]);
            return new ObjectPacket<>(user);
        } catch (RowNotFoundException ignored) {
            return new ErrorPacket("User not found");
        }
    }
}
