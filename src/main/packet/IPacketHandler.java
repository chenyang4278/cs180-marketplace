package packet;

/**
 * IPacketHandler
 *
 * @author Ayden Cline
 * @version 4/12/25
 */
public interface IPacketHandler {
    Packet handle(Packet packet, String[] args);
    String[] match(String matchingPath);
}
