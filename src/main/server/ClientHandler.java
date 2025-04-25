package server;

import packet.*;
import packet.response.ErrorPacket;
import server.handlers.*;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

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
    public static PacketHandler[] handlers = new PacketHandler[] {
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
        new LoginHandler(),
        new ImageUploadHandler(),
        new ImageDownloadHandler(),
        new GetInboxUsersHandler()
    };

    private Socket socket;
    private OutputStream oStream;
    private InputStream iStream;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    // parse packet body into a file
    // may or may not be part of a file-upload endpoint
    // if not upload-related, it'll be cleaned up afterward (file deleted)
    private String parsePacketBody(Packet packet) throws PacketParsingException, ErrorPacketException {
        if (packet.getBody().length == 0) {
            return null;
        }

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) { // should not occur
            throw new RuntimeException(e);
        }

        try {
            File file = new File("static/tmp-" + HandlerUtil.generateToken(16));
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);

            Packet currentPacket = packet;
            while (true) {
                byte[] body = currentPacket.getBody();

                if (body.length != 0) {
                    digest.update(body);
                    fos.write(body);
                }

                if (!currentPacket.getBodyContinues()) {
                    fos.close();

                    String fileHash = HandlerUtil.hex(digest.digest());
                    File staticFile = new File("static/" + fileHash);

                    if (staticFile.exists()) {
                        file.delete();
                    } else if (!file.renameTo(staticFile)) {
                        System.out.println("Unable to rename file");
                        file.delete();
                        return null;
                    }

                    packet.addHeader("File-Hash", fileHash);
                    return fileHash;
                }

                currentPacket = readPacket();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendFile(String fileHash, Packet packet) {
        File file = new File("static/" + fileHash);
        if (!file.isFile()) {
            return;
        }

        try {
            FileInputStream fis = new FileInputStream(file);

            packet.setBodyContinues(true);

            byte[] buf = new byte[1024 * 1024];
            int count;
            while ((count = fis.read(buf)) > 0) {
                if (count == buf.length) {
                    packet.setBody(buf);
                } else {
                    packet.setBody(Arrays.copyOfRange(buf, 0, count));
                }

                sendPacket(packet);

                packet = new Packet();
                packet.setBodyContinues(true);
            }

            packet.setBodyContinues(false);
            sendPacket(packet);

            fis.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // if the file downloaded was not part of an upload
    // then delete it
    private void cleanupFile(Packet resp, String hash) {
        PacketHeader header = resp.getHeader("File-Hash");
        if (header == null) {
            return;
        }

        new File("/static/" + hash).delete();
    }

    private void handlePacket(Packet packet) throws IOException, PacketParsingException, ErrorPacketException {
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

        // parse body for binary data
        String hash = parsePacketBody(packet);
        // hand off packet to handler
        Packet resp = handler.handle(packet, args);
        // determine whether the endpoint is returning a file
        PacketHeader downloadHeader = resp.getHeader("Download-Hash");
        if (downloadHeader != null) {
            sendFile(downloadHeader.getValues().get(0), resp);
        } else {
            sendPacket(resp);
        }

        if (hash != null) {
            cleanupFile(resp, hash);
        }
    }

    public void run() {
        try {
            while (true) {
                Packet packet = readPacket();

                System.out.println("Received packet for path " + packet.getPath());

                handlePacket(packet);
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