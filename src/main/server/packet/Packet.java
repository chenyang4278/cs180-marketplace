package server.packet;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;
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

    public Packet(String body) {
        this.path = "";
        this.headers = new ArrayList<>();
        this.body = body;
    }

    static Packet fromString(String data) throws IllegalArgumentException, ErrorPacketException {
        int pos = 0;

        String[] lines = data.split("\n");
        if (lines.length < 2) {
            throw new IllegalArgumentException("Invalid packet");
        }

        if (!lines[0].startsWith("/")) {
            throw new IllegalArgumentException("Invalid packet");
        }

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
                throw new IllegalArgumentException("Invalid packet");
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
    public void write(Writer writer) throws IOException {
        char[] data = toString().toCharArray();

        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(data.length);

        writer.write(buffer.asCharBuffer().array());
        writer.write(data);
    }

    public static Packet read(Reader reader) throws IOException, PacketParsingException, ErrorPacketException {
        ByteBuffer buf = ByteBuffer.allocate(Integer.BYTES);
        if (reader.read(buf.asCharBuffer()) != Integer.BYTES) {
            throw new PacketParsingException("Failed to read packet length");
        }

        char[] data = new char[buf.getInt()];
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
            builder.append(header.toString()).append("\n");
        }
        builder.append("\n");

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
