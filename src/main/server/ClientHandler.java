package server;

import server.packet.*;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    public static PacketHandler[] handlers = new PacketHandler[] {
        new GetUserHandler()
    };

    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            Packet packet = readPacket();

            PacketHandler handler = null;
            String[] args = null;

            for (PacketHandler h : handlers) {
                String[] result = h.match(packet.getPath());
                if (result != null) {
                    handler = h;
                    args = result;
                    break;
                }
            }

            if (handler == null) {
                sendPacket(new ErrorPacket("Path not found"));
                return;
            }

            sendPacket(handler.handle(packet, args));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (PacketParsingException e) {
            try {
                sendPacket(new ErrorPacket("Bad packet formatting: " + e.getMessage()));
            } catch (IOException e2) {
                e.printStackTrace();
            }
        } catch (ErrorPacketException e) {
            // error packets from the *client* shouldn't be sent in the first place
            e.printStackTrace();
        }
    }

    private Packet readPacket() throws IOException, PacketParsingException, ErrorPacketException {
        try (InputStreamReader reader = new InputStreamReader(socket.getInputStream())) {
            return Packet.read(reader);
        }
    }

    private void sendPacket(Packet packet) throws IOException {
        try (OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream())) {
            packet.write(writer);
        }
    }
}
