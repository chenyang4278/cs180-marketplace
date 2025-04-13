package packet;

import database.DatabaseWrapper;

/**
 * PacketHandler
 * <p>
 * Abstract class for handling incoming client packets.
 *
 * @author Ayden Cline
 * @version 4/12/25
 */
public abstract class PacketHandler implements IPacketHandler {
    private final String path;
    protected DatabaseWrapper db;

    public PacketHandler(String path) {
        this.path = path;

        db = DatabaseWrapper.get();
    }

    /**
     * Returns whether the given path matches the path
     * of this handler. If so, returns any arguments in
     * the path
     *
     * @param matchingPath path to check for a match
     * @return String[] if match, otherwise null
     */
    public String[] match(String matchingPath) {
        String[] pathParts = path.split("/");
        String[] matchingPathParts = matchingPath.split("/");

        if (pathParts.length != matchingPathParts.length) {
            return null;
        }

        int nArgs = 0;
        for (String part : pathParts) {
            if (!part.isEmpty() && part.charAt(0) == ':') {
                nArgs++;
            }
        }

        String[] args = new String[nArgs];
        int argsI = 0;
        for (int i = 0; i < pathParts.length; i++) {
            String part = pathParts[i];
            String matchingPart = matchingPathParts[i];

            if (!part.isEmpty() && part.charAt(0) == ':') {
                args[argsI] = matchingPart;
                argsI++;
            } else if (!part.equals(matchingPart)) {
                return null;
            }
        }

        return args;
    }
}

