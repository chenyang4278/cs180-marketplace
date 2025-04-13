package packet;

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
