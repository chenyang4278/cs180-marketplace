package server.handlers;

import database.RowNotFoundException;
import database.User;
import packet.Packet;
import packet.PacketHandler;
import packet.response.ErrorPacket;
import packet.response.ObjectPacket;

public class GetUserHandler extends PacketHandler {
    public GetUserHandler() {
        super("/users/:id");
    }

    @Override
    public Packet handle(Packet packet, String[] args) {
        try {
            System.out.println(args[0]);
            User user = db.getByColumn(User.class, "id", args[0]);
            return new ObjectPacket<>(user);
        } catch (RowNotFoundException ignored) {
            return new ErrorPacket("User not found");
        }
    }
}
