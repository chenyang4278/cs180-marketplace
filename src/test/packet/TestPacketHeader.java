package packet;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * TestPacketHeader Class
 * <p>
 * A class to test JUnit tests for packet headers.
 *
 * @author Karma Luitel, lab L24
 * @version 4/12/25
 */
public class TestPacketHeader {
    @Test
    public void testConstructors() {
        PacketHeader ph = new PacketHeader("packetheader", "value 1", "value 2");
        assertEquals("packetheader", ph.getName());
        assertEquals("value 1", ph.getValues().get(0));
        assertEquals("value 2", ph.getValues().get(1));

        PacketHeader ph2 = new PacketHeader("header");
        assertEquals("header", ph2.getName());
    }

    @Test
    public void testGettersAndSetters() {
        PacketHeader ph = new PacketHeader("packetheader", "value1", "value 2");
        assertEquals("packetheader", ph.getName());
        ph.addValue("value 3");
        assertEquals("value 3", ph.getValues().get(2));
    }
}
