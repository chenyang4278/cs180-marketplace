import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

public class TestDatabaseWrapper {
    private TestTable[] tables = new TestTable[4];

    private void initTables() {
        try {
            tables[0] = new TestTable("hi", Integer.MIN_VALUE, Long.MAX_VALUE, Float.MIN_VALUE, Double.MAX_VALUE);
            tables[1] = new TestTable("hi2", 0, 0, 0, 0);
            tables[2] = new TestTable("hi3", Integer.MAX_VALUE, Long.MIN_VALUE, Float.MAX_VALUE, Double.MIN_VALUE);
            tables[3] = new TestTable("hi4", Integer.MIN_VALUE, Long.MAX_VALUE, Float.MIN_VALUE, Double.MAX_VALUE);
            for (TestTable table : tables) {
                table.save();
            }
        } catch (DatabaseWriteException e) {
            e.printStackTrace();
        }
    }

    private TestTable[] getTables() {
        if (tables[0] == null) {
            initTables();
        }

        return tables;
    }

    @BeforeClass
    public static void setUpClass() {
        // start on a clean slate
        File f = new File("TestTable.csv");
        f.delete();
    }

    @Test
    public void testGetById() throws RowNotFoundException {
        TestTable[] tables = getTables();

        DatabaseWrapper db = DatabaseWrapper.get();
        for (TestTable table : tables) {
            TestTable tableFromDb = db.getById(TestTable.class, table.getId());
            assert tableFromDb != null;
            assert tableFromDb.equals(table);
        }
    }

    @Test
    public void testGetByColumn() throws RowNotFoundException {
        TestTable[] tables = getTables();

        DatabaseWrapper db = DatabaseWrapper.get();
        for (TestTable table : tables) {
            TestTable tableFromDb = db.getByColumn(
                    TestTable.class,
                    "long_count",
                    String.valueOf(table.getLongCount())
            );
            assert tableFromDb != null;
            assert tableFromDb.getLongCount() == table.getLongCount();
        }
    }

    @Test
    public void testUpdate() throws RowNotFoundException, DatabaseWriteException {
        TestTable table = getTables()[0];
        table.setName("updated hi");
        table.setCount(0);
        table.setLongCount(10);
        table.setDecimal(5.62f);
        table.setPreciseDecimal(10.55d);
        table.save();

        TestTable tableFromDb = DatabaseWrapper.get().getById(TestTable.class, table.getId());
        assert tableFromDb != null;
        assert table.equals(tableFromDb);
    }

    @Test
    public void testFilter() {
        getTables();  // ensure initiated
        List<TestTable> tables = DatabaseWrapper.get().filterByColumn(TestTable.class, "count", "0");
        for (TestTable table : tables) {
            assert table.getCount() == 0;
        }
    }

    @Test
    public void testDelete() throws DatabaseWriteException {
        TestTable table = getTables()[0];
        table.delete();
        try {
            TestTable.getById(table.getId());
            fail("User was not deleted");
        } catch (RowNotFoundException e) {
        }

        // should do nothing
        table.delete();
    }

    @Test
    public void testDoesNotExist() {
        getTables();  // ensure initiated
        try {
            TestTable.getById(-1);
            fail("Should have thrown an exception for no object with id -1");
        } catch (RowNotFoundException e) {
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

        List<TestTable> tables = DatabaseWrapper.get().filterByColumn(TestTable.class, "count", "727");
        assertEquals(250, tables.size());
    }

    class TestThread extends Thread {
        private String name;

        public TestThread(String name) {
            this.name = name;
        }

        public void run() {
            try {
                String localName = "thread-safety-test-table-" + name;

                for (int i = 0; i < 100; i++) {
                    TestTable t = new TestTable(
                            localName,
                            100,
                            200,
                            0.54f,
                            0.218d
                    );
                    t.save();
                    t.setCount(727);
                    t.save();
                }

                DatabaseWrapper db = DatabaseWrapper.get();
                List<TestTable> tables = db.filterByColumn(TestTable.class, "name", localName);
                assertEquals(100, tables.size());
                for (TestTable table : tables) {
                    assertEquals(727, table.getCount());
                }

                for (int i = 0; i < tables.size(); i += 2) {
                    TestTable t = tables.get(i);
                    t.delete();
                }

                tables = db.filterByColumn(TestTable.class, "name", localName);
                assertEquals(50, tables.size());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
