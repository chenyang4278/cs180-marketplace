package packet;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Packet implements IPacket {
    private String path;
    private List<PacketHeader> headers;
    private String body;

    public Packet(String path, List<PacketHeader> headers, String body) {
        this.path = path;
        this.headers = headers;
        this.body = body;
    }

    public Packet(String path) {
        this.path = path;
        this.headers = new ArrayList<>();
        this.body = "";
    }

    public Packet() {
        this.path = "";
        this.headers = new ArrayList<>();
        this.body = "";
    }

    static Packet fromString(String data) throws PacketParsingException, ErrorPacketException {
        int pos = 0;

        String[] lines = data.split("\n");

        String path = lines[0];

        pos += path.length() + 1;

        // header parsing
        // initiate with max capacity that may be used
        ArrayList<PacketHeader> headers = new ArrayList<PacketHeader>(lines.length - 1);
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            pos += line.length() + 1;

            if (line.isEmpty()) {
                break;
            }

            String[] parts = line.split(":", 2);
            if (parts.length != 2) {
                throw new PacketParsingException("Invalid packet");
            }

            String name = parts[0];
            String value = parts[1];

            boolean headerExists = false;
            for (PacketHeader header : headers) {
                if (header.getName().equals(name)) {
                    header.addValue(value);
                    headerExists = true;
                    break;
                }
            }

            if (!headerExists) {
                headers.add(new PacketHeader(name, value));
            }
        }

        String body = data.substring(pos);

        for (PacketHeader header : headers) {
            if (header.getName().equals("Status") && !header.getValues().get(0).equals("OK")) {
                throw new ErrorPacketException(body);
            }
        }

        return new Packet(path, headers, body);
    }

    @Override
    public void addHeader(String name, String value) {
        for (PacketHeader header : headers) {
            if (header.getName().equals(name)) {
                header.addValue(value);
                return;
            }
        }

        headers.add(new PacketHeader(name, value));
    }

    @Override
    public void write(OutputStream stream) throws IOException {
        char[] data = toString().toCharArray();

        ObjectOutputStream oos = new ObjectOutputStream(stream);
        oos.writeInt(data.length);
        oos.flush();

        OutputStreamWriter writer = new OutputStreamWriter(stream);

        writer.write(data);
        writer.flush();
    }

    public static Packet read(InputStream stream) throws IOException, PacketParsingException, ErrorPacketException {
        ObjectInputStream ois = new ObjectInputStream(stream);
        int packetLength = ois.readInt();

        InputStreamReader reader = new InputStreamReader(stream);

        char[] data = new char[packetLength];
        if (reader.read(data) != data.length) {
            throw new PacketParsingException("Failed to read full packet data");
        }

        return Packet.fromString(String.valueOf(data));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(path).append("\n");

        for (PacketHeader header : headers) {
            builder.append(header.toString());
        }
        builder.append("\n").append(body);

        return builder.toString();
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public List<PacketHeader> getHeaders() {
        return headers;
    }

    @Override
    public String getBody() {
        return body;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public void setHeaders(List<PacketHeader> headers) {
        this.headers = headers;
    }

    @Override
    public void setBody(String body) {
        this.body = body;
    }
}
