package server.handlers;

import database.RowNotFoundException;
import database.User;
import packet.factory.ErrorPacketFactory;
import packet.factory.ObjectPacketFactory;
import packet.Packet;
import packet.PacketHandler;

public class GetUserHandler extends PacketHandler {
    public GetUserHandler() {
        super("/users/:id");
    }

    @Override
    public Packet handle(Packet packet, String[] args) {
        try {
            System.out.println(args[0]);
            User user = db.getByColumn(User.class, "id", args[0]);
            return new ObjectPacketFactory<>(user).create();
        } catch (RowNotFoundException ignored) {
            return new ErrorPacketFactory("User not found").create();
        }
    }
}
