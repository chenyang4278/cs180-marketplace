package server.packet;

import database.DatabaseWrapper;

public abstract class PacketHandler implements IPacketHandler {
    private String path;
    protected DatabaseWrapper db;

    public PacketHandler(String path) {
        this.path = path;

        db = DatabaseWrapper.get();
    }

    public String[] match(String matchingPath) {
        String[] pathParts = path.split("/");
        String[] matchingPathParts = matchingPath.split("/");

        if (pathParts.length != matchingPathParts.length) {
            return null;
        }

        int nArgs = 0;
        for (String part : pathParts) {
            if (part.charAt(0) == ':') {
                nArgs++;
            }
        }

        String[] args = new String[nArgs];
        for (int i = 0; i < pathParts.length; i++) {
            if (pathParts[i].charAt(0) == ':') {
                args[0] = matchingPathParts[i];
            } else if (!pathParts[i].equals(matchingPathParts[i])) {
                return null;
            }
        }

        return args;
    }
}
