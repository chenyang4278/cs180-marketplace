package server.packet;

public interface IPacketHandler {
    Packet handle(Packet packet, String[] args);
    String[] match(String matchingPath);
}
