package database;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;

import static org.junit.Assert.*;

/**
 * TestBaseDatabase Class
 * <p>
 * A class to test JUnit tests for Database.java
 *
 * @author Karma Luitel, lab L24
 * @version 3/28/25
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestBaseDatabase {

    @Test
    public void testACreate() throws DatabaseNotFoundException {
        new File("userDb.csv").delete();

        String[] headers = {"username", "password", "balance", "rating"};
        Database userDb = new Database("userDb.csv", headers);


        String[] user1 = {"karma", "", "821.21", "12"};
        String[] user2 = {"kar3ma", "passw2ord123", "821.21", "12"};
        String[] user3 = {"ka2rma", "pass2word123", "821.21", "12"};
        String[] user4 = {"ka\"2\"rma\",\"", "password123", "821.21", "12"};
        String[] user5 = {"ka2rma", "pass2word123", "821.21", "12"};
        String[] user6 = {"ka2rma", "pass2word123", "821.21", "12"};
        String[] user7 = {"ka\"2\"rma\",\"", "passw2ord123", "7821.21", "12"};
        String[] user8 = {"k\\\na\"2\"r\nma\\\n\",\"", "p\\ass\nw2or\\nd123\n", "7821.21", "12"};

        userDb.write(user1);
        userDb.write(user2);
        userDb.write(user3);
        userDb.write(user4);
        userDb.write(user5);
        userDb.write(user6);
        userDb.write(user7);
        userDb.write(user8);

        assertTrue(true);
    }

    @Test
    public void testBRead() throws DatabaseNotFoundException {

        String[] headers = {"username", "password", "balance", "rating"};
        Database userDb = new Database("userDb.csv", headers);

        assertArrayEquals(new String[]{"karma", "", "821.21", "12"},
                userDb.get("username", "karma", false).get(0));
        assertArrayEquals(new String[]{"ka\"2\"rma\",\"", "password123", "821.21", "12"},
                userDb.get("username", "ka\"2\"rma\",\"", false).get(0));
        assertArrayEquals(new String[]{"ka\"2\"rma\",\"", "passw2ord123", "7821.21", "12"},
                userDb.get("username", "k A\"2\"r Ma\",\"  ", true).get(1));
        assertArrayEquals(new String[]{"ka\"2\"rma\",\"", "password123", "821.21", "12"},
                userDb.get("password", "Pa sswo rD123\n", true).get(0));
        assertArrayEquals(new String[]{"k\\\na\"2\"r\nma\\\n\",\"", "p\\ass\nw2or\\nd123\n", "7821.21", "12"},
                userDb.get("password", "p\\ass\nw2or\\nd123\n", false).get(0));

        Database userDbDne = new Database("userDbDne.csv", headers);
        Exception exception = assertThrows(DatabaseNotFoundException.class, () -> {
            userDbDne.get("password", "password123", false).get(0);
        });
        assertNotNull(exception);
    }

    @Test
    public void testCReadAll() throws DatabaseNotFoundException {
        String[] headers = {"username", "password", "balance", "rating"};
        Database userDb = new Database("userDb.csv", headers);

        assertEquals(8, userDb.getAll().size());
        assertArrayEquals(new String[]{"ka2rma", "pass2word123", "821.21", "12"},
                userDb.getAll().get(2));
    }

    @Test
    public void testDUpdate() throws DatabaseNotFoundException {

        String[] headers = {"username", "password", "balance", "rating"};
        Database userDb = new Database("userDb.csv", headers);

        userDb.update("username", "ka\"2\"rma\",\"", "balance", "122");
        userDb.update("username", "kar3ma", "password", "12212");
        assertEquals("122", userDb.get("balance", "122", false).get(0)[2]);
        assertEquals("12212", userDb.get("password", "12212", false).get(0)[1]);

        userDb.update("username", "kar3ma",
                new String[]{"ka\"2\"rma\",\"", "password123", "821.21", "1234"});
        assertEquals("1234", userDb.get("rating", "1234", false).get(0)[3]);

        userDb.update("username", "ka\"2\"rma\",\"", "balance",
                "12");

        Database userDbDne = new Database("userDbDne.csv", headers);
        Exception exception = assertThrows(DatabaseNotFoundException.class, () -> {
            userDbDne.update("username", "ka\"2\"rma\",\"", "balance",
                    "12");
        });
        assertNotNull(exception);

        Database userDbDne2 = new Database("userDbDne.csv", headers);
        Exception exception2 = assertThrows(DatabaseNotFoundException.class, () -> {
            userDbDne2.update("username", "kar3ma",
                    new String[]{"ka\"2\"rma\",\"", "password123", "821.21", "1234"});

        });
        assertNotNull(exception2);

    }

    @Test
    public void testEDelete() throws DatabaseNotFoundException {

        String[] headers = {"username", "password", "balance", "rating"};
        Database userDb = new Database("userDb.csv", headers);

        userDb.delete("username", "ka\"2\"rma\",\"");
        userDb.delete("password", "pass2word123");

        assertEquals("[]", userDb.get("username", "ka\"2\"rma\",\"", false).toString());
        assertEquals("[]", userDb.get("password", "pass2word123", false).toString());

        userDb.delete("rating", "12");
        assertEquals("[]", userDb.get("rating", "12", false).toString());
        assertEquals("[]", userDb.get("username", "kar3ma", false).toString());

        Database userDbDne = new Database("userDbDne.csv", headers);
        Exception exception = assertThrows(DatabaseNotFoundException.class, () -> {
            userDbDne.delete("rating", "12");
        });
        assertNotNull(exception);
    }

    @Test
    public void testFFilename() {
        String[] headers = {"username", "password", "balance", "rating"};
        Database userDb = new Database("userDb.csv", headers);
        assertEquals("userDb.csv", userDb.getFilename());
        userDb.setFilename("main_db.csv");
        assertEquals("main_db.csv", userDb.getFilename());
    }

    @Test
    public void testGHeaders() {
        String[] headers = {"username", "password", "balance", "rating"};
        Database userDb = new Database("userDb.csv", headers);
        assertArrayEquals(headers, userDb.getHeaders());
        String[] headers2 = {"username2", "password2", "balance2", "rating2"};
        userDb.setHeaders(headers2);
        assertArrayEquals(headers2, userDb.getHeaders());
    }
}