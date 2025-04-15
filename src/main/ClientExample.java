import data.Listing;
import data.User;
import packet.ErrorPacketException;
import packet.Packet;
import packet.PacketHeader;
import packet.PacketParsingException;
import packet.response.ObjectPacket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

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

        ArrayList<PacketHeader> headers = new ArrayList<PacketHeader>();
        PacketHeader ph1 = new PacketHeader("username", "karma");
        PacketHeader ph2 = new PacketHeader("password", "123456");
        headers.add(ph1);
        headers.add(ph2);
        new Packet("/user/create", headers).write(os);
        ObjectPacket<User> packet = Packet.read(is);
        User u = packet.getObj();
        System.out.println(u.getId());

        headers.clear();
        headers.add(new PacketHeader("username", "karma"));
        headers.add(new PacketHeader("title", "10 apples"));
        headers.add(new PacketHeader("description", "10 granny smith apples, sour"));
        headers.add(new PacketHeader("price", "1.10"));
        headers.add(new PacketHeader("image", "null"));

        new Packet("/listing/create", headers).write(os);
        ObjectPacket<Listing> packet2 = Packet.read(is);
        Listing l = packet2.getObj();
        //would now add listing to user obj
        System.out.println(l.getDescription());


    }
}
