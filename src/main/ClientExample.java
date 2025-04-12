import database.User;
import packet.ErrorPacketException;
import packet.Packet;
import packet.PacketParsingException;
import packet.factory.ObjectPacketFactory;

import java.io.IOException;
import java.net.Socket;

public class ClientExample {
    public static void main(String[] args) throws IOException, PacketParsingException, ErrorPacketException {
        Socket socket = new Socket("localhost", 8727);

        // send request to /users/:id endpoint
        new Packet("/users/10").write(socket.getOutputStream());

        // receive response
        Packet resp = Packet.read(socket.getInputStream());
        // parse response with ObjectPacketFactory<User>
        ObjectPacketFactory<User> parsedResp = ObjectPacketFactory.parse(User.class, resp);

        System.out.println("Returned user " + parsedResp.getObj().getUsername());
    }
}
