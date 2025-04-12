package server.handlers;

import packet.*;
import packet.factory.ErrorPacketFactory;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    public static PacketHandler[] handlers = new PacketHandler[] {
        new GetUserHandler()
    };

    private Socket socket;
    private OutputStream oStream;
    private InputStream iStream;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            while (true) {
                Packet packet = readPacket();

                System.out.println("Received packet for path " + packet.getPath());

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
                    sendPacket(new ErrorPacketFactory("Path not found").create());
                } else {
                    sendPacket(handler.handle(packet, args));
                }
            }
        } catch (PacketParsingException e) {
            try {
                sendPacket(new ErrorPacketFactory("Bad packet formatting: " + e.getMessage()).create());
            } catch (IOException e2) {
                e.printStackTrace();
            }
        } catch (IOException ignored) {
            System.out.println("Connection closed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Packet readPacket() throws IOException, PacketParsingException, ErrorPacketException {
        if (iStream == null) {
            iStream = socket.getInputStream();
        }

        return Packet.read(iStream);
    }

    private void sendPacket(Packet packet) throws IOException {
        if (oStream == null) {
            oStream = socket.getOutputStream();
        }

        packet.write(oStream);
    }
}
