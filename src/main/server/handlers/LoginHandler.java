package server.handlers;

import data.Session;
import data.User;
import database.DatabaseWrapper;
import database.DatabaseWriteException;
import packet.Packet;
import server.PacketHandler;
import packet.response.ErrorPacket;
import packet.response.ObjectPacket;
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
        String[] data = packet.getHeaderValues("username", "password");
        if (data == null) {
            return new ErrorPacket("Invalid username or password");
        }

        String username = data[0];
        String password = data[1];

        // look for user by username
        ArrayList<User> users = new ArrayList<>(db.filterByColumn(User.class,
                "username", username));
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
                db.delete(session);
            }

            // make sure duplicate token isn't added
            // although the chances of that are so astronomically low
            // that it's basically impossible
            String token;
            do {
                token = HandlerUtil.generateToken();
            } while (!db.filterByColumn(Session.class, "token", token).isEmpty());

            Session session = new Session(user.getId(), token);
            db.save(session);

            Packet resp = new ObjectPacket<User>(user);
            resp.addHeader("Session-Token", session.getToken());
            return resp;
        } catch (DatabaseWriteException e) {
            return new ErrorPacket("Server error, failed to login");
        }
    }
}
