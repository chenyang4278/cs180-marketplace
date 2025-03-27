import org.junit.*;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

public class TestBaseDatabase {

    @Test
    public void testWrite() throws DatabaseNotFoundException{

        String[] headers = {"username", "password", "balance", "rating"};
        BaseDatabase user_db = new BaseDatabase("user_db.txt", headers);

       
        String[] user1 = {"karma", "pass2word123", "821.21", "12"};
        String[] user2 = {"kar3ma", "passw2ord123", "821.21", "12"};
        String[] user3 = {"ka2rma", "pass2word123", "821.21", "12"};
        String[] user4 = {"ka\"2\"rma\",\"", "password123", "821.21", "12"};
        String[] user5 = {"ka2rma", "pass2word123", "821.21", "12"};
        String[] user6 = {"ka2rma", "pass2word123", "821.21", "12"};
        String[] user7 = {"ka\"2\"rma\",\"", "passw2ord123", "7821.21", "12"};

        user_db.write(user1);
        user_db.write(user2);
        user_db.write(user3);
        user_db.write(user4);
        user_db.write(user5);
        user_db.write(user6);
        user_db.write(user7);

        assertTrue(true);
    }

    @Test
    public void testRead() throws DatabaseNotFoundException {  

        String[] headers = {"username", "password", "balance", "rating"};
        BaseDatabase user_db = new BaseDatabase("user_db.txt", headers);

        assertArrayEquals(new String[] {"ka\"2\"rma\",\"", "password123", "821.21", "12"}, user_db.get("username", "ka\"2\"rma\",\"").get(0));
        assertArrayEquals(new String[] {"ka\"2\"rma\",\"", "passw2ord123", "7821.21", "12"}, user_db.get("username", "ka\"2\"rma\",\"").get(1));
        assertArrayEquals(new String[] {"ka\"2\"rma\",\"", "password123", "821.21", "12"},user_db.get("password", "password123").get(0));
    }

    @Test
    public void testDelete() throws DatabaseNotFoundException {  

        String[] headers = {"username", "password", "balance", "rating"};
        BaseDatabase user_db = new BaseDatabase("user_db.txt", headers);

        user_db.delete("username", "ka\"2\"rma\",\"");
        user_db.delete("password", "pass2word123");

        assertEquals("[]",user_db.get("username", "ka\"2\"rma\",\"").toString());
        assertEquals("[]",user_db.get("password", "pass2word123").toString());
        assertNotSame("[]",user_db.get("username", "kar3ma").toString());

        user_db.delete("rating", "12");
        assertEquals("[]",user_db.get("rating", "12").toString());
        assertEquals("[]",user_db.get("username", "kar3ma").toString());
    }

}
