import database.User;
import packet.ErrorPacketException;
import packet.Packet;
import packet.PacketParsingException;
import packet.response.ObjectPacket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientExample {
    public static void main(String[] args) throws IOException, PacketParsingException, ErrorPacketException {
        Socket socket = new Socket("localhost", 8727);

        OutputStream os = socket.getOutputStream();
        InputStream is = socket.getInputStream();

        // send request to /users/:id endpoint
        new Packet("/path").write(os);

        // receive response
        try {
            Packet.read(is);
        } catch (ErrorPacketException e) {
            System.out.println(e.getMessage());
        }

        new Packet("/users/10").write(os);

        ObjectPacket<User> packet = Packet.read(is);
        System.out.println("Received user: " + packet.getObj().getUsername());
    }
}
