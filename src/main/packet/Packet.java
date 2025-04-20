package packet;

import packet.response.ErrorPacket;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Packet
 * <p>
 * Object that is sent between server and client and
 * can be extended to create packets with more info.
 *
 * @author Ayden Cline
 * @version 4/12/25
 */
public class Packet implements IPacket, Serializable {
    // path where the packet is intended to be handled at
    private String path;
    // any kind of data that's not binary
    private List<PacketHeader> headers;
    // binary data
    private byte[] body;
    // indicating whether the server should expect another
    // data packet after this one
    private boolean bodyContinues;

    public Packet(String path, List<PacketHeader> headers) {
        this.path = path;
        this.headers = headers;
        this.body = new byte[0];
        this.bodyContinues = false;
    }

    public Packet(String path) {
        this.path = path;
        this.headers = new ArrayList<>();
        this.body = new byte[0];
        this.bodyContinues = false;
    }

    public Packet() {
        this.path = "";
        this.headers = new ArrayList<>();
        this.body = new byte[0];
        this.bodyContinues = false;
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
    public PacketHeader getHeader(String name) {
        for (PacketHeader header : headers) {
            if (header.getName().equalsIgnoreCase(name)) {
                return header;
            }
        }

        return null;
    }

    /**
     * Writes this packet to an output stream
     *
     * @param stream stream to write to
     * @throws IOException possibly thrown while writing
     */
    @Override
    public void write(OutputStream stream) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(stream);

        oos.writeObject(this);
        oos.flush();
    }

    /**
     * Reads a packet from an input stream
     *
     * @param stream stream to read from
     * @param <T>    type of packet to attempt to read
     * @return read packet
     * @throws IOException            possibly thrown while reading
     * @throws PacketParsingException thrown if the object read isn't a valid packet object
     * @throws ErrorPacketException   thrown if an error packet is returned
     */
    public static <T extends Packet> T read(InputStream stream) throws IOException,
            PacketParsingException, ErrorPacketException {
        ObjectInputStream ois = new ObjectInputStream(stream);
        try {
            Packet packet = (Packet) ois.readObject();

            PacketHeader status = packet.getHeader("Status");
            if (status != null && status.getValues().get(0).equals("ERR")) {
                throw new ErrorPacketException(((ErrorPacket) packet).getMessage());
            }

            return (T) packet;
        } catch (ClassNotFoundException e) {
            throw new PacketParsingException("Invalid packet");
        }
    }

    /**
     * Get the first value of each header specified,
     * or null if one of the headers does not exist or
     * one of the header values is empty.
     *
     * @param keys list of header keys
     * @return header values, taking the first from each header
     */
    public String[] getHeaderValues(String... keys) {
        String[] values = new String[keys.length];

        for (int i = 0; i < keys.length; i++) {
            PacketHeader header = getHeader(keys[i]);
            if (header == null) {
                return null;
            }

            String value = header.getValues().get(0);
            if (value.trim().isEmpty()) {
                return null;
            }

            values[i] = value;
        }

        return values;
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
    public byte[] getBody() {
        return body;
    }

    @Override
    public boolean getBodyContinues() {
        return bodyContinues;
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
    public void setBody(byte[] body) {
        this.body = body;
    }

    @Override
    public void setBodyContinues(boolean bodyContinues) {
        this.bodyContinues = bodyContinues;
    }
}
