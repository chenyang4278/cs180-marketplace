package data;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * TestSession Class
 * <p>
 * A class to test JUnit tests for sessions.
 *
 * @author Ayden Cline, lab L24
 * @version 4/12/25
 */
public class TestSession {
    @Test
    public void testCreation() {
        Session session = new Session(1, "token");
        assertEquals(1, session.getUserId());
        assertEquals("token", session.getToken());
    }

    @Test
    public void testGettersAndSetters() {
        Session session = new Session();
        session.setUserId(1);
        session.setToken("token");
        assertEquals(1, session.getUserId());
        assertEquals("token", session.getToken());
    }
}
