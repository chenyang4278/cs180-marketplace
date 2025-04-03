import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestBaseDatabase {

    @Test
    public void testACreate() throws DatabaseNotFoundException{

        String[] headers = {"username", "password", "balance", "rating"};
        Database user_db = new Database("user_db.csv", headers);

       
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
    public void testBRead() throws DatabaseNotFoundException {  

        String[] headers = {"username", "password", "balance", "rating"};
        Database user_db = new Database("user_db.csv", headers);

        assertArrayEquals(new String[] {"ka\"2\"rma\",\"", "password123", "821.21", "12"}, user_db.get("username", "ka\"2\"rma\",\"").get(0));
        assertArrayEquals(new String[] {"ka\"2\"rma\",\"", "passw2ord123", "7821.21", "12"}, user_db.get("username", "ka\"2\"rma\",\"").get(1));
        assertArrayEquals(new String[] {"ka\"2\"rma\",\"", "password123", "821.21", "12"},user_db.get("password", "password123").get(0));

        Database user_db_dne = new Database("user_db_dne.csv", headers);
        Exception exception = assertThrows(DatabaseNotFoundException.class, () -> {
            user_db_dne.get("password", "password123").get(0);
        });
        assertNotNull(exception);
    }

    @Test
    public void testCUpdate() throws DatabaseNotFoundException {  

        String[] headers = {"username", "password", "balance", "rating"};
        Database user_db = new Database("user_db.csv", headers);

        user_db.update("username", "ka\"2\"rma\",\"", "balance", "122");
        user_db.update("username", "kar3ma", "password", "12212");
        assertEquals("122",user_db.get("balance", "122").get(0)[2]);
        assertEquals("12212",user_db.get("password", "12212").get(0)[1]);

        user_db.update("username", "kar3ma", new String[] {"ka\"2\"rma\",\"", "password123", "821.21", "1234"});
        assertEquals("1234",user_db.get("rating", "1234").get(0)[3]);

        user_db.update("username", "ka\"2\"rma\",\"", "balance", "12");

        Database user_db_dne = new Database("user_db_dne.csv", headers);
        Exception exception = assertThrows(DatabaseNotFoundException.class, () -> {
            user_db_dne.update("username", "ka\"2\"rma\",\"", "balance", "12");
        });
        assertNotNull(exception);

        Database user_db_dne_2 = new Database("user_db_dne.csv", headers);
        Exception exception_2 = assertThrows(DatabaseNotFoundException.class, () -> {
            user_db_dne_2.update("username", "kar3ma", new String[] {"ka\"2\"rma\",\"", "password123", "821.21", "1234"});

        });
        assertNotNull(exception_2);

    }

    @Test
    public void testDDelete() throws DatabaseNotFoundException {  

        String[] headers = {"username", "password", "balance", "rating"};
        Database user_db = new Database("user_db.csv", headers);

        user_db.delete("username", "ka\"2\"rma\",\"");
        user_db.delete("password", "pass2word123");

        assertEquals("[]",user_db.get("username", "ka\"2\"rma\",\"").toString());
        assertEquals("[]",user_db.get("password", "pass2word123").toString());

        user_db.delete("rating", "12");
        assertEquals("[]",user_db.get("rating", "12").toString());
        assertEquals("[]",user_db.get("username", "kar3ma").toString());

        Database user_db_dne = new Database("user_db_dne.csv", headers);
        Exception exception = assertThrows(DatabaseNotFoundException.class, () -> {
            user_db_dne.delete("rating", "12");
        });
        assertNotNull(exception);
    }

}
