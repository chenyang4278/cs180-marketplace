import org.junit.Test;

import static org.junit.Assert.*;

/**
 * UserTest
 * <p>
 * Junit tests for user class
 *
 * @author Chen Yang, section 24
 * @version 4/1/25
 */

public class UserTest {

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
    }

    @Test
    public void testUpdateBalance() {
        User user = new User("ez", "securepass");

        assertEquals(0.0, user.getBalance(), 0.01);

        user.updateBalance(100.0);
        assertEquals(100.0, user.getBalance(), 0.01);

        user.updateBalance(-50.0);
        assertEquals(50.0, user.getBalance(), 0.01);
    }

    @Test
    public void testUpdateRating() {
        User user = new User("hello", "password");

        assertEquals(0.0, user.getRating(), 0.01);

        user.updateRating(4.5);
        assertEquals(4.5, user.getRating(), 0.01);

        user.updateRating(5.0);
        assertEquals(5.0, user.getRating(), 0.01);
    }

    @Test
    public void testAddRemoveListing() {
        User user = new User("Awooga", "lol");
        Listing l = new Listing(user.getId(), "Awooga",
                "Water Bottle", "holds water", 20.00,
                false);
        user.createListing(l);
        assertEquals(1, user.getListings().size());

        user.removeListing(l);
        assertEquals(0, user.getListings().size());
    }

    @Test
    public void testAddRemoveMessage() {
        User user = new User("tom", "tompwd");
        user.sendMessage("Hello, how are you?", 2);
        assertEquals(1, user.getInbox().size());
        user.removeMessage(user.getInbox().get(0));
        assertEquals(0, user.getInbox().size());
    }

    @Test
    public void testDeleteAccount() {
        User user = new User("lucy", "lucypassword");
        user.createListing(new Listing(user.getId(), "Lucy", "Book", "A good book", 10.00, false));
        user.sendMessage("This is a test message", 2);
        user.deleteAccount();
        assertNull(user.getUsername());
        assertNull(user.getPassword());
        assertEquals(0.0, user.getBalance(), 0.01);
        assertEquals(0.0, user.getRating(), 0.01);
        assertTrue(user.getListings().isEmpty());
        assertTrue(user.getInbox().isEmpty());
    }

    @Test
    public void testInvalidUsername() {
        try {
            User user = new User("", "password");
            fail("Expected IllegalArgumentException for empty username");
        } catch (IllegalArgumentException e) {
            assertEquals("Username cannot be empty", e.getMessage());
        }
    }

    @Test
    public void testInvalidPassword() {
        try {
            User user = new User("validuser", "");
            fail("Expected IllegalArgumentException for empty password");
        } catch (IllegalArgumentException e) {
            assertEquals("Password cannot be empty", e.getMessage());
        }
    }

}
