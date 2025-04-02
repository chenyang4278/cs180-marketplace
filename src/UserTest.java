import org.junit.Test;
import static org.junit.Assert.*;

public class UserTest {

    @Test
    public void testUserCreation(){
        User user  = new User("chen_yang","passwordabc");
        assertNotNull(user);
        assertEquals("chen_yang",user.getUsername());
        assertEquals("passwordabc", user.getPassword());
        assertEquals(0.0, user.getBalance(), 0.01);
        assertEquals(0.0, user.getRating(), 0.01);
    }

    @Test
    public void testSettersAndGetters(){
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

}
