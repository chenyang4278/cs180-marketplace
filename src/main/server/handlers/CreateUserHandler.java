package server.handlers;

import database.DatabaseWrapper;
import database.DatabaseWriteException;
import database.RowNotFoundException;
import database.User;
import packet.Packet;
import packet.PacketHandler;
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
        super("/usercreate/");
    }

    /*
     * Expected PacketHeaders:
     * username - arg in index 0
     * password - arg in index 0
     */
    @Override
    public Packet handle(Packet packet, String[] args) {
        String username = packet.getHeader("username").getValues().get(0);
        String password = packet.getHeader("password").getValues().get(0);
        try {
            User user = db.getByColumn(User.class, "username", username);
            return new ErrorPacket("Username already exists!");
        } catch (RowNotFoundException ignored) {
            User u = new User(username, password);
            try {
                DatabaseWrapper.get().save(u);
                return new ObjectPacket<User>(u);
            } catch (DatabaseWriteException e) {
                return new ErrorPacket("Database faliure in creating user");
            }

        }
    }
}