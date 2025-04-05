import org.junit.Test;

import static org.junit.Assert.*;

public class MessageTest {
    @Test
    public void testSetAndGetSenderId() {
        Message msg = new Message(1, 1, "message");
        msg.setSenderId(2);
        assertEquals(2, msg.getSenderId());
    }

    @Test
    public void testSetAndGetReceiverId() {
        Message msg = new Message(1, 1, "message");
        msg.setReceiverId(3);
        assertEquals(3, msg.getReceiverId());
    }

    @Test
    public void testSetAndGetMessage() {
        Message msg = new Message(1, 1, "message");
        msg.setMessage("new message");
        assertEquals("new message", msg.getMessage());
    }

    @Test
    public void testSetAndGetTimestamp() {
        long now = System.currentTimeMillis();
        Message msg = new Message(1, 1, "message");
        msg.setTimestamp(now);
        assertEquals(now, msg.getTimestamp());
    }
}
