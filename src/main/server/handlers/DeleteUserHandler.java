package server.handlers;

import database.DatabaseWrapper;
import database.User;
import database.DatabaseWriteException;
import database.RowNotFoundException;
import packet.Packet;
import packet.PacketHandler;
import packet.response.ErrorPacket;
import packet.response.SuccessPacket;

public class DeleteUserHandler extends PacketHandler {
    public DeleteUserHandler() {
        super("/userdelete/");
    }

    /*
     * Expected PacketHeaders:
     * username - arg in index 0
     */
    @Override
    public Packet handle(Packet packet, String[] args) {
        String username = packet.getHeader("username").getValues().get(0);
        try {
            User u = db.getByColumn(User.class, "username", username);
            try {
                DatabaseWrapper.get().delete(u);
                return new SuccessPacket();
            } catch (DatabaseWriteException e) {
                return new ErrorPacket("Database delete faliure!");
            }
        } catch (RowNotFoundException ignored) {
            return new ErrorPacket("User does not exist!");
        }
    }
}