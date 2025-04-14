package client;

import packet.*;
import packet.response.*;

import java.io.*;
import java.net.*;
import java.util.*;
import database.Table;


/**
 * Client
 * <p>
 * Handles sending and receiving packets to/from the server.
 * Does not store data locally; interacts with server for all data.
 *
 * @author Chen
 * @version 4/13/25
 */
public class Client {
    private Socket socket;
    private OutputStream oStream;
    private InputStream iStream;

    public Client(String host, int port) throws IOException {
        socket = new Socket(host, port);
        oStream = socket.getOutputStream();
        iStream = socket.getInputStream();
    }

    public <T extends Table> T sendObjectPacketRequest(String path, List<PacketHeader> headers, Class<T> type) throws IOException, PacketParsingException, ErrorPacketException {
        Packet packet = new Packet(path, headers);
        packet.write(oStream);
        ObjectPacket<T> response = Packet.read(iStream);
        return response.getObj();
    }

    public <T extends Table> List<T> sendObjectListPacketRequest(String path, List<PacketHeader> headers, Class<T> type) throws IOException, PacketParsingException, ErrorPacketException {
        Packet packet = new Packet(path, headers);
        packet.write(oStream);
        ObjectListPacket<T> response = Packet.read(iStream);
        return response.getObjList();
    }

    public SuccessPacket sendSuccessPacketRequest(String path, List<PacketHeader> headers) throws IOException, PacketParsingException, ErrorPacketException {
        Packet packet = new Packet(path, headers);
        packet.write(oStream);
        return Packet.read(iStream);
    }

    public void close() throws IOException {
        iStream.close();
        oStream.close();
        socket.close();
    }

    public static List<PacketHeader> createHeaders(String... keyValuePairs) {
        List<PacketHeader> headers = new ArrayList<>();
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            headers.add(new PacketHeader(keyValuePairs[i], keyValuePairs[i + 1]));
        }
        return headers;
    }
}
