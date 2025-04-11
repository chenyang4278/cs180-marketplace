package database;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * TestSerializable
 * <p>
 * A class that handles a JUnit tests for the Serializable class.
 *
 * @author Ayden Cline
 * @version 3/31/25
 */
public class TestSerializable {
    @Test
    public void testGetColumns() {
        assertArrayEquals(
                new String[]{"id", "name", "count", "long_count", "decimal", "precise_decimal"},
                Serializable.getColumns(TestTable.class)
        );
    }

    @Test
    public void testAsRow() {
        TestTable t = new TestTable("table1", 10, 30, 5.4f, 3.2d);
        assertArrayEquals(
                new String[]{String.valueOf(t.getId()), "table1", "10", "30", "5.4", "3.2"},
                t.asRow()
        );
    }

    @Test
    public void testFromRow() {
        TestTable t = Serializable.fromRow(
                TestTable.class,
                new String[]{"1", "table1", "10", "30", "5.4", "3.2"}
        );
        assertEquals(1, t.getId());
        assertEquals("table1", t.getName());
        assertEquals(10, t.getCount());
        assertEquals(30, t.getLongCount());
        assertEquals(5.4, t.getDecimal(), 0.001);
        assertEquals(3.2, t.getPreciseDecimal(), 0.001);

        try {
            Serializable.fromRow(
                    TestTable.class,
                    new String[]{}
            );
            fail("Should have thrown an exception for invalid number of values");
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testToAndBack() {
        TestTable t1 = new TestTable("table1", 10, 30, 5.4f, 3.2d);
        TestTable t2 = Serializable.fromRow(TestTable.class, t1.asRow());
        assertTrue(t1.equals(t2));
    }
}
