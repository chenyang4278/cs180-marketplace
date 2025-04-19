package packet;
import data.Session;
import data.User;
import database.DatabaseWrapper;
import database.DatabaseWriteException;
import org.junit.Test;
import server.handlers.HandlerUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * TestPacket Class
 * <p>
 * A class to test JUnit tests for packets.
 *
 * @author Karma Luitel, lab L24
 * @version 4/12/25
 */
public class TestPacket {

    @Test
    public void testPacketCreation() {
        Packet p = new Packet();
        assertEquals("", p.getPath());
        assertEquals(0, p.getHeaders().size());

        Packet p2 = new Packet("mypath");
        assertEquals("mypath", p2.getPath());
        assertEquals(0, p2.getHeaders().size());

        ArrayList<PacketHeader> headers = new ArrayList<PacketHeader>();
        headers.add(new PacketHeader("test", "val"));
        Packet p3 = new Packet("mypath2", headers);
        assertEquals("mypath2", p3.getPath());
        assertEquals(headers, p3.getHeaders());
    }

    @Test
    public void testHeader() {
        ArrayList<PacketHeader> headers = new ArrayList<PacketHeader>();
        headers.add(new PacketHeader("testHeader", "first header"));
        Packet p = new Packet("mypath", headers);
        p.addHeader("testHeader", "second header");
        assertEquals(2, p.getHeader("testHeader").getValues().size());
        assertEquals("second header", p.getHeader("testHeader").getValues().get(1));
    }

    @Test
    public void testGettersAndSetters() {
        ArrayList<PacketHeader> headers = new ArrayList<PacketHeader>();
        headers.add(new PacketHeader("testHeader", "first header"));
        Packet p = new Packet("mypath", headers);
        p.setPath("newpath");
        assertEquals("newpath", p.getPath());
        ArrayList<PacketHeader> headers2 = new ArrayList<PacketHeader>();
        headers2.add(new PacketHeader("testHeader2", "first header2"));
        p.setHeaders(headers2);
        assertEquals(headers2, p.getHeaders());
    }

    @Test
    public void testWriteAndRead() {
        try {
            ArrayList<PacketHeader> headers = new ArrayList<PacketHeader>();
            headers.add(new PacketHeader("testHeader", "first header"));
            headers.add(new PacketHeader("testHeader2", "first header 2"));
            Packet p = new Packet("mypath", headers);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            p.write(outputStream);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            Packet p2 = Packet.read(inputStream);
            assertEquals("mypath", p2.getPath());
            assertEquals(2, p2.getHeaders().size());
            assertEquals("first header 2", p2.getHeader("testHeader2").getValues().get(0));
        } catch (Exception e) {
            fail("Exception during test: " + e.getMessage());
        }
    }

    @Test
    public void testGetHeaderValues() {
        try {
            ArrayList<PacketHeader> headers = new ArrayList<PacketHeader>();
            headers.add(new PacketHeader("testHeader", "first header"));
            headers.add(new PacketHeader("testHeader2", "first header 2"));
            Packet p = new Packet("mypath", headers);
            String[] vals = p.getHeaderValues("testHeader", "testHeader2");
            assertEquals("first header", vals[0]);
            assertEquals("first header 2", vals[1]);
        } catch (Exception e) {
            fail("Exception during test: " + e.getMessage());
        }
    }
}
