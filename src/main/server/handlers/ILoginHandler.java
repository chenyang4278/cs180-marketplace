package server.handlers;

import packet.Packet;

/**
 * ILoginHandler
 * <p>
 * Interface for the login handler.
 *
 * @author Ayden Cline
 * @version 4/14/25
 */
public interface ILoginHandler {
    Packet handle(Packet packet, String[] args);
}
