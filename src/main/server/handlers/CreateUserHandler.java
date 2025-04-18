package server.handlers;

import database.DatabaseWrapper;
import database.DatabaseWriteException;
import database.RowNotFoundException;
import data.User;
import packet.Packet;
import server.PacketHandler;
import packet.response.ErrorPacket;
import packet.response.ObjectPacket;

/**
 * CreateUserHandler
 * <p>
 * Handles creating users.
 *
 * @author Karma Luitel
 * @version 4/13/25
 */
public class CreateUserHandler extends PacketHandler implements ICreateUserHandler {
    public CreateUserHandler() {
        super("/users/create");
    }

    /*
     * Expected headers:
     * username
     * password
     */
    @Override
    public Packet handle(Packet packet, String[] args) {
        String[] data = packet.getHeaderValues("username", "password");
        if (data == null) {
            return new ErrorPacket("Invalid username or password");
        }

        String username = data[0];
        String password = data[1];

        try {
            db.getByColumn(User.class, "username", username);
            return new ErrorPacket("Username already exists!");
        } catch (RowNotFoundException ignored) {
            User user = new User(username, HandlerUtil.hashPassword(password));
            try {
                db.save(user);
                return new ObjectPacket<User>(user);
            } catch (DatabaseWriteException e) {
                return new ErrorPacket("Database failure in creating user");
            }
        }
    }
}