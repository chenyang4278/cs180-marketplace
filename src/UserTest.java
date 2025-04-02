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

}
