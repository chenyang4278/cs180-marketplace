package database;

import java.io.Serializable;

/**
 * ISerializable
 * <p>
 * A interface for a Serializable class, used to aid database operations.
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
