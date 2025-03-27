import org.junit.*;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class TestBaseDatabase {

    @Test
    public void testWrite() throws DatabaseNotFoundException{

        ArrayList<String> headers = new ArrayList<String>();
        headers.add("username");
        headers.add("password");
        headers.add("balance");
        headers.add("rating");
        BaseDatabase user_db = new BaseDatabase("user_db.txt", headers);

        ArrayList<String> user1 = new ArrayList<String>();
        user1.add("karma");
        user1.add("pass2word123");
        user1.add("821.21");
        user1.add("12");

        ArrayList<String> user2 = new ArrayList<String>();
        user2.add("kar3ma");
        user2.add("passw2ord123");
        user2.add("821.21");
        user2.add("12");

        ArrayList<String> user3 = new ArrayList<String>();
        user3.add("ka2rma");
        user3.add("password123");
        user3.add("821.21");
        user3.add("12");

        ArrayList<String> user4 = new ArrayList<String>();
        user4.add("ka\"2\"rma\",\"");
        user4.add("password123");
        user4.add("821.21");
        user4.add("12");

        ArrayList<String> user5 = new ArrayList<String>();
        user5.add("ka2rma");
        user5.add("pas2sword123");
        user5.add("821.21");
        user5.add("12");

        ArrayList<String> user6 = new ArrayList<String>();
        user6.add("ka2rma");
        user6.add("password123");
        user6.add("821.21");
        user6.add("12");

        ArrayList<String> user7 = new ArrayList<String>();
        user7.add("ka\"2\"rma\",\"");
        user7.add("passw2ord123");
        user7.add("7821.21");
        user7.add("12");

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

        ArrayList<String> headers = new ArrayList<String>();
        headers.add("username");
        headers.add("password");
        headers.add("balance");
        headers.add("rating");
        BaseDatabase user_db = new BaseDatabase("user_db.txt", headers);

        assertEquals("[[ka\"2\"rma\",\", password123, 821.21, 12], [ka\"2\"rma\",\", passw2ord123, 7821.21, 12]]",user_db.get("username", "ka\"2\"rma\",\"").toString());
        assertEquals("[[ka2rma, password123, 821.21, 12], [ka\"2\"rma\",\", password123, 821.21, 12], [ka2rma, password123, 821.21, 12]]",user_db.get("password", "password123").toString());
    }

    @Test
    public void testDelete() throws DatabaseNotFoundException {  

        ArrayList<String> headers = new ArrayList<String>();
        headers.add("username");
        headers.add("password");
        headers.add("balance");
        headers.add("rating");
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
