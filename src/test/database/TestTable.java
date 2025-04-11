package database;

/**
 * TestTable
 * Note that the sole purpose of this class is to be used for testing
 * DatabaseWrapper and Serializable,
 * so it does not have an interfaces or actual use in our project.
 *
 * @author Ayden Cline
 * @version 3/31/25
 */
public class TestTable extends Serializable {
    @SerializableField(field = "name", index = 1)
    private String name;

    @SerializableField(field = "count", index = 2)
    private int count;

    @SerializableField(field = "long_count", index = 3)
    private long longCount;

    @SerializableField(field = "decimal", index = 4)
    private float decimal;

    @SerializableField(field = "precise_decimal", index = 5)
    private double preciseDecimal;

    public TestTable() {
    }

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
        return (name.equals(other.name) &&
                count == other.count &&
                longCount == other.longCount &&
                decimal == other.decimal &&
                preciseDecimal == other.preciseDecimal);
    }

    public static TestTable getById(int id) throws RowNotFoundException {
        return DatabaseWrapper.get().getById(TestTable.class, id);
    }
}
