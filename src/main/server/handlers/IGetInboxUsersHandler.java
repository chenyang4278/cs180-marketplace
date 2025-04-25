package server.handlers;

import packet.Packet;

public interface IGetInboxUsersHandler {
    Packet handle(Packet packet, String[] args);
}
