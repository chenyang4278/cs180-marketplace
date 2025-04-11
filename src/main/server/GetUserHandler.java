package server;

import database.RowNotFoundException;
import database.User;
import server.packet.ErrorPacket;
import server.packet.ObjectPacket;
import server.packet.Packet;
import server.packet.PacketHandler;

public class GetUserHandler extends PacketHandler {
    public GetUserHandler() {
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
