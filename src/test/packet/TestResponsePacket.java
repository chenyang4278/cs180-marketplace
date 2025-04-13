package packet;
import org.junit.Test;

import packet.response.ErrorPacket;
import packet.response.ObjectPacket;
import packet.response.SuccessPacket;

import static org.junit.Assert.*;

import database.TestingClass;

/**
 * TestResponsePacket Class
 * <p>
 * A class to test JUnit tests for response packets.
 *
 * @author Karma Luitel, lab L24
 * @version 4/12/25
 */
public class TestResponsePacket {
    
    @Test
    public void testErrorPacket() {
        ErrorPacket ep = new ErrorPacket("error");
        assertEquals("error", ep.getMessage());
        ep.setMessage("error2");
        assertEquals("error2", ep.getMessage());
        assertEquals("ERR", ep.getHeader("Status").getValues().get(0));
    }

    @Test
    public void testObjPacket() {
        TestingClass testObject = new TestingClass("hi", Integer.MIN_VALUE, Long.MAX_VALUE, Float.MIN_VALUE, Double.MAX_VALUE);
        ObjectPacket<TestingClass> op = new ObjectPacket<TestingClass>(testObject);
        assertEquals(testObject, op.getObj());
        TestingClass testObject2 = new TestingClass("hi2", Integer.MIN_VALUE, Long.MAX_VALUE, Float.MIN_VALUE, Double.MAX_VALUE);
        op.setObj(testObject2);
        assertEquals(testObject2, op.getObj());
    }

    @Test
    public void testSuccessPacket() {
        SuccessPacket sp = new SuccessPacket();
        assertEquals("OK", sp.getHeader("Status").getValues().get(0));
    }
}
