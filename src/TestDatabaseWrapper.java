import org.junit.Test;

import java.util.List;

class TestTable extends Serializable {
    @SerializableField( field = "name", index = 1 )
    private String name;

    @SerializableField( field = "count", index = 2 )
    private int count;

    @SerializableField( field = "long_count", index = 3 )
    private long longCount;

    @SerializableField( field = "decimal", index = 4 )
    private float decimal;

    @SerializableField( field = "precise_decimal", index = 5 )
    private double preciseDecimal;

    public TestTable() {}

    public TestTable(String name, int count, long longCount, float decimal, double preciseDecimal) {
        this.name = name;
        this.count = count;
        this.longCount = longCount;
        this.decimal = decimal;
        this.preciseDecimal = preciseDecimal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getLongCount() {
        return longCount;
    }

    public void setLongCount(long longCount) {
        this.longCount = longCount;
    }

    public float getDecimal() {
        return decimal;
    }

    public void setDecimal(float decimal) {
        this.decimal = decimal;
    }

    public double getPreciseDecimal() {
        return preciseDecimal;
    }

    public void setPreciseDecimal(double preciseDecimal) {
        this.preciseDecimal = preciseDecimal;
    }

    public boolean equals(TestTable other) {
        return (
            name.equals(other.name) &&
                count == other.count &&
                longCount == other.longCount &&
                decimal == other.decimal &&
                preciseDecimal == other.preciseDecimal
            );
    }

    public static TestTable getById(int id) throws RowNotFoundException {
        return DatabaseWrapper.get().getById(TestTable.class, id);
    }
}

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
    public void testFilter() throws RowNotFoundException {
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
            assert false;
        } catch (RowNotFoundException e) {}
    }
}
