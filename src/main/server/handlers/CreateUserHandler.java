package server.handlers;

import database.DatabaseWrapper;
import database.DatabaseWriteException;
import database.RowNotFoundException;
import data.User;
import packet.Packet;
import packet.PacketHandler;
import packet.PacketHeader;
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
     * Expected PacketHeaders:
     * username - arg in index 0
     * password - arg in index 0
     */
    @Override
    public Packet handle(Packet packet, String[] args) {
        PacketHeader usernameHeader = packet.getHeader("username");
        PacketHeader passwordHeader = packet.getHeader("password");
        if (usernameHeader == null || passwordHeader == null) {
            return new ErrorPacket("Invalid username or password");
        }

        String username = usernameHeader.getValues().get(0);
        String password = passwordHeader.getValues().get(0);

        if (username.isEmpty() || password.isEmpty()) {
            return new ErrorPacket("Invalid username or password");
        }

        try {
            db.getByColumn(User.class, "username", username);
            return new ErrorPacket("Username already exists!");
        } catch (RowNotFoundException ignored) {
            User user = new User(username, HandlerUtil.hashPassword(password));
            try {
                user.save();
                return new ObjectPacket<User>(user);
            } catch (DatabaseWriteException e) {
                return new ErrorPacket("Database failure in creating user");
            }
        }
    }
}