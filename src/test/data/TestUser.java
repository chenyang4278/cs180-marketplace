package data;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * TestUser
 * <p>
 * Junit tests for user class
 *
 * @author Chen Yang, section 24
 * @version 4/1/25
 */
public class TestUser {

    @Test
    public void testUserCreation() {
        User user = new User("chen_yang", "passwordabc");
        assertNotNull(user);
        assertEquals("chen_yang", user.getUsername());
        assertEquals("passwordabc", user.getPassword());
        assertEquals(0.0, user.getBalance(), 0.01);
        assertEquals(0.0, user.getRating(), 0.01);
    }

    @Test
    public void testSettersAndGetters() {
        User user = new User("dawn_xin", "mypassword");

        user.setUsername("new_username");
        assertEquals("new_username", user.getUsername());

        user.setPassword("newpassword123");
        assertEquals("newpassword123", user.getPassword());

        user.setBalance(122.1);
        assertEquals(122.1, user.getBalance(), 0.01);

        user.setRating(52.1);
        assertEquals(52.1, user.getRating(), 0.01);
    }
}
