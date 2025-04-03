import org.junit.Test;

import java.util.List;

import static org.junit.Assert.fail;

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

    @Test
    public void testGetById() throws RowNotFoundException {
        TestTable[] tables = getTables();

        IDatabaseWrapper db = DatabaseWrapper.get();
        for (TestTable table : tables) {
            TestTable tableFromDb = db.getById(TestTable.class, table.getId());
            assert tableFromDb != null;
            assert tableFromDb.equals(table);
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
        } catch (RowNotFoundException e) {}
    }
}
