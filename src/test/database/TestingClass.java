package database;

import data.Table;
import data.TableField;

/**
 * TestingClass
 * Note that the sole purpose of this class is to be used for testing
 * so it does not have an interfaces or actual use in our project.
 *
 * @author Ayden Cline
 * @version 3/31/25
 */
public class TestingClass extends Table {
    @TableField(field = "name", index = 1)
    private String name;

    @TableField(field = "count", index = 2)
    private int count;

    @TableField(field = "long_count", index = 3)
    private long longCount;

    @TableField(field = "decimal", index = 4)
    private float decimal;

    @TableField(field = "precise_decimal", index = 5)
    private double preciseDecimal;

    public TestingClass() {
    }

    public TestingClass(String name, int count, long longCount, float decimal, double preciseDecimal) {
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

    public boolean equals(TestingClass other) {
        return (name.equals(other.name) &&
                count == other.count &&
                longCount == other.longCount &&
                decimal == other.decimal &&
                preciseDecimal == other.preciseDecimal);
    }

    public static TestingClass getById(int id) throws RowNotFoundException {
        return DatabaseWrapper.get().getById(TestingClass.class, id);
    }
}
