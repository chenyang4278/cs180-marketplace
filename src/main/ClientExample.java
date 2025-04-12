import packet.response.ErrorPacket;
import packet.ErrorPacketException;
import packet.Packet;
import packet.PacketParsingException;

import java.io.IOException;
import java.net.Socket;

public class ClientExample {
    public static void main(String[] args) throws IOException, PacketParsingException, ErrorPacketException {
        Socket socket = new Socket("localhost", 8727);

        // send request to /users/:id endpoint
        new Packet("/path").write(socket.getOutputStream());

        // receive response
        Packet resp = Packet.read(socket.getInputStream());
    }
}
