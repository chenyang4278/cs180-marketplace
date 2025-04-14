package data;

import database.TestingClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * TestTable
 * <p>
 * A class that handles a JUnit tests for the Table class.
 *
 * @author Ayden Cline
 * @version 3/31/25
 */
public class TestTable {
    @Test
    public void testGetColumns() {
        assertArrayEquals(
                new String[]{"id", "name", "count", "long_count", "decimal", "precise_decimal"},
                Table.getColumns(TestingClass.class)
        );
    }

    @Test
    public void testAsRow() {
        TestingClass t = new TestingClass("table1", 10, 30, 5.4f, 3.2d);
        assertArrayEquals(
                new String[]{String.valueOf(t.getId()), "table1", "10", "30", "5.4", "3.2"},
                t.asRow()
        );
    }

    @Test
    public void testFromRow() {
        TestingClass t = Table.fromRow(
                TestingClass.class,
                new String[]{"1", "table1", "10", "30", "5.4", "3.2"}
        );
        assertEquals(1, t.getId());
        assertEquals("table1", t.getName());
        assertEquals(10, t.getCount());
        assertEquals(30, t.getLongCount());
        assertEquals(5.4, t.getDecimal(), 0.001);
        assertEquals(3.2, t.getPreciseDecimal(), 0.001);

        try {
            Table.fromRow(
                    TestingClass.class,
                    new String[]{}
            );
            fail("Should have thrown an exception for invalid number of values");
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testToAndBack() {
        TestingClass t1 = new TestingClass("table1", 10, 30, 5.4f, 3.2d);
        TestingClass t2 = Table.fromRow(TestingClass.class, t1.asRow());
        assertTrue(t1.equals(t2));
    }

    @Test
    public void testSetId() {
        TestingClass t1 = new TestingClass("table1", 10, 30, 5.4f, 3.2d);
        t1.setId(-1);
        assertEquals(-1, t1.getId());
    }
}
