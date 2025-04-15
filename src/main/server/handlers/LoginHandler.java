package server.handlers;

import data.Session;
import data.User;
import database.DatabaseWriteException;
import packet.Packet;
import packet.PacketHandler;
import packet.PacketHeader;
import packet.response.ErrorPacket;
import packet.response.ObjectPacket;
import packet.response.SuccessPacket;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;

/**
 * LoginHandler
 *
 * @author Ayden Cline
 * @version 4/14/25
 */
public class LoginHandler extends PacketHandler implements ILoginHandler {
    public LoginHandler() {
        super("/login");
    }

    @Override
    public Packet handle(Packet packet, String[] args) {
        // get login credentials
        PacketHeader usernameHeader = packet.getHeader("username");
        PacketHeader passwordHeader = packet.getHeader("password");
        if (usernameHeader == null || passwordHeader == null) {
            return new ErrorPacket("Invalid username or password");
        }

        String username = usernameHeader.getValues().get(0);
        String password = passwordHeader.getValues().get(0);

        // look for user by username
        ArrayList<User> users = User.getUsersByColumn("username", username);
        if (users.isEmpty()) {
            return new ErrorPacket("Invalid username or password");
        }

        User user = users.get(0);

        // check if password matches
        if (!HandlerUtil.hashPassword(password).equals(user.getPassword())) {
            return new ErrorPacket("Invalid username or password");
        }

        // save session and return new session token
        try {
            for (Session session : db.filterByColumn(Session.class, "user_id", Integer.toString(user.getId()))) {
                session.delete();
            }

            // make sure duplicate token isn't added
            // although the chances of that are so astronomically low
            // that it's basically impossible
            String token;
            do {
                token = HandlerUtil.generateToken();
            } while (!db.filterByColumn(Session.class, "token", token).isEmpty());

            Session session = new Session(user.getId(), token);
            session.save();

            Packet resp = new ObjectPacket<User>(user);
            resp.addHeader("Session-Token", session.getToken());
            return resp;
        } catch (DatabaseWriteException e) {
            return new ErrorPacket("Server error, failed to login");
        }
    }
}
