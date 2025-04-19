package database;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

/**
 * TestDatabaseWrapper
 * <p>
 * A class that handles a JUnit tests for the DatabaseWrapper class.
 *
 * @author Ayden Cline
 * @version 3/31/25
 */
public class TestDatabaseWrapper {
    private TestingClass[] tables = new TestingClass[4];

    private void initTables() {
        try {
            tables[0] = new TestingClass("hi", Integer.MIN_VALUE, Long.MAX_VALUE, Float.MIN_VALUE, Double.MAX_VALUE);
            tables[1] = new TestingClass("hi2", 0, 0, 0, 0);
            tables[2] = new TestingClass("hi3", Integer.MAX_VALUE, Long.MIN_VALUE, Float.MAX_VALUE, Double.MIN_VALUE);
            tables[3] = new TestingClass("hi4", Integer.MIN_VALUE, Long.MAX_VALUE, Float.MIN_VALUE, Double.MAX_VALUE);
            for (TestingClass table : tables) {
                DatabaseWrapper.get().save(table);
            }
        } catch (DatabaseWriteException e) {
            e.printStackTrace();
        }
    }

    private TestingClass[] getTables() {
        if (tables[0] == null) {
            initTables();
        }

        return tables;
    }

    @BeforeClass
    public static void setUpClass() {
        // start on a clean slate
        File f = new File("TestingClass.csv");
        f.delete();
    }

    @Test
    public void testGetById() throws RowNotFoundException {
        TestingClass[] lTables = getTables();

        DatabaseWrapper db = DatabaseWrapper.get();
        for (TestingClass table : lTables) {
            TestingClass tableFromDb = db.getById(TestingClass.class, table.getId());
            assert tableFromDb != null;
            assert tableFromDb.equals(table);
        }
    }

    @Test
    public void testGetByColumn() throws RowNotFoundException {
        TestingClass[] lTables = getTables();

        DatabaseWrapper db = DatabaseWrapper.get();
        for (TestingClass table : lTables) {
            TestingClass tableFromDb = db.getByColumn(
                    TestingClass.class,
                    "long_count",
                    String.valueOf(table.getLongCount()));
            assert tableFromDb != null;
            assert tableFromDb.getLongCount() == table.getLongCount();
        }
    }

    @Test
    public void testSetById() throws DatabaseWriteException, RowNotFoundException {
        TestingClass table = getTables()[0];

        DatabaseWrapper db = DatabaseWrapper.get();
        db.setById(TestingClass.class, table.getId(), "long_count", "-101829192");
        db.setById(TestingClass.class, table.getId(), "decimal", "121.2189721211");

        TestingClass tableFromDb = db.getById(TestingClass.class, table.getId());
        assert tableFromDb != null;
        assert tableFromDb.getLongCount() == -101829192;
        assert Math.abs(tableFromDb.getDecimal() - 121.2189721211) < 0.000001;

        try {
            db.setById(TestingClass.class, table.getId(), "auwhdaa", "121.2189721211");
            fail();
        } catch (DatabaseWriteException e) {
            assertTrue(true);
        }

    }

    @Test
    public void testUpdate() throws RowNotFoundException, DatabaseWriteException {
        TestingClass table = getTables()[0];
        table.setName("updated hi");
        table.setCount(0);
        table.setLongCount(10);
        table.setDecimal(5.62f);
        table.setPreciseDecimal(10.55d);
        DatabaseWrapper.get().save(table);


        TestingClass tableFromDb = DatabaseWrapper.get().getById(TestingClass.class, table.getId());
        assert tableFromDb != null;
        assert table.equals(tableFromDb);
    }

    @Test
    public void testFilter() {
        getTables(); // ensure initiated
        List<TestingClass> lTables = DatabaseWrapper.get().filterByColumn(TestingClass.class, "count", "0");
        for (TestingClass table : lTables) {
            assert table.getCount() == 0;
        }
    }

    @Test
    public void testDelete() throws DatabaseWriteException {
        TestingClass table = getTables()[0];
        DatabaseWrapper.get().delete(table);
        try {
            TestingClass.getById(table.getId());
            fail("User was not deleted");
        } catch (RowNotFoundException e) {
            System.out.println(e.getMessage());
        }

        // should do nothing
        DatabaseWrapper.get().delete(table);
    }

    @Test
    public void testDoesNotExist() {
        getTables(); // ensure initiated
        try {
            TestingClass.getById(-1);
            fail("Should have thrown an exception for no object with id -1");
        } catch (RowNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testThreadSafety() throws InterruptedException {
        Thread[] threads = new Thread[]{
                new TestThread("1"),
                new TestThread("2"),
                new TestThread("3"),
                new TestThread("4"),
                new TestThread("5")
        };

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        List<TestingClass> lTables = DatabaseWrapper.get().filterByColumn(TestingClass.class, "count", "727");
        assertEquals(250, lTables.size());
    }

    /**
     * TestThread
     * <p>
     * A class used solely for testing the thread safety of the DatabaseWrapper, so it has no interface/documentation.
     * Does not have any other use in our project.
     *
     * @author Ayden Cline
     * @version 3/31/25
     */
    class TestThread extends Thread {
        private String name;

        public TestThread(String name) {
            this.name = name;
        }

        public void run() {
            try {
                String localName = "thread-safety-test-table-" + name;

                for (int i = 0; i < 100; i++) {
                    TestingClass t = new TestingClass(
                            localName,
                            100,
                            200,
                            0.54f,
                            0.218d);
                    DatabaseWrapper.get().save(t);
                    t.setCount(727);
                    DatabaseWrapper.get().save(t);
                }

                DatabaseWrapper db = DatabaseWrapper.get();
                List<TestingClass> lTables = db.filterByColumn(TestingClass.class, "name", localName);
                assertEquals(100, lTables.size());
                for (TestingClass table : lTables) {
                    assertEquals(727, table.getCount());
                }

                for (int i = 0; i < lTables.size(); i += 2) {
                    TestingClass t = lTables.get(i);
                    DatabaseWrapper.get().delete(t);
                }

                lTables = db.filterByColumn(TestingClass.class, "name", localName);
                assertEquals(50, lTables.size());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
