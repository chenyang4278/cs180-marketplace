package database;

import java.io.Serializable;

/**
 * ITable
 * <p>
 * A interface for a Table class, used to aid database operations.
 *
 * @author Ayden Cline
 * @version 3/31/25
 */
public interface ITable extends Serializable {
    int getId();

    void setId(int id);

    String[] asRow();

    void save() throws DatabaseWriteException;

    void delete() throws DatabaseWriteException;
}
