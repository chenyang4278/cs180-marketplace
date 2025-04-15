package server.handlers;

import packet.Packet;

public interface ILoginHandler {
    Packet handle(Packet packet, String[] args);
}
