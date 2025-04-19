package server;

import packet.*;
import packet.response.ErrorPacket;
import server.handlers.*;

import java.io.*;
import java.net.Socket;

/**
 * ClientHandler
 * <p>
 * Does initial handling of packets coming from clients.
 * The packets are then sent to their proper handler or
 * a path not found error is returned.
 * Note that since this course does not expect to test over network io, and
 * this class just handles network packets, it has no testcases.
 *
 * @author Ayden Cline
 * @version 4/12/25
 */
public class ClientHandler implements Runnable, IClientHandler {
    public static PacketHandler[] handlers = new PacketHandler[]{
            new BuyListingHandler(),
            new CreateListingHandler(),
            new CreateMessageHandler(),
            new CreateUserHandler(),
            new DeleteListingHandler(),
            new DeleteUserHandler(),
            new EditListingHandler(),
            new EditUserHandler(),
            new GetListingsFromAttributeHandler(),
            new GetMessagesBetweenUsersHandler(),
            new GetUserFromIdHandler(),
            new GetUsersFromAttributeHandler(),
            new LoginHandler()
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
                    sendPacket(new ErrorPacket("Path not found"));
                } else {
                    sendPacket(handler.handle(packet, args));
                }
            }
        } catch (PacketParsingException e) {
            try {
                sendPacket(new ErrorPacket("Bad packet formatting: " + e.getMessage()));
            } catch (IOException e2) {
                e.printStackTrace();
            }
        } catch (IOException e) {
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