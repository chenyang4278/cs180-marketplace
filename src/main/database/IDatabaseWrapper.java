package database;

import java.util.List;

/**
 * IDatabaseWrapper
 * <p>
 * A interface for a DatabaseWrapper class.
 *
 * @author Ayden Cline
 * @version 3/31/25
 */
public interface IDatabaseWrapper {
    <T extends Serializable> T getByColumn(Class<T> cls, String column, String value) throws RowNotFoundException;

    <T extends Serializable> List<T> filterByColumn(Class<T> cls, String column, String value);

    <T extends Serializable> T getById(Class<T> cls, int id) throws RowNotFoundException;

    <T extends Serializable> void save(T obj) throws DatabaseWriteException;

    <T extends Serializable> void delete(T obj) throws DatabaseWriteException;
}
