package database;

import data.Table;

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
    <T extends Table> T getByColumn(Class<T> cls, String column, String value) throws RowNotFoundException;

    <T extends Table> List<T> filterByColumn(Class<T> cls, String column, String value);

    <T extends Table> T getById(Class<T> cls, int id) throws RowNotFoundException;

    <T extends Table> void save(T obj) throws DatabaseWriteException;

    <T extends Table> void delete(T obj) throws DatabaseWriteException;
}
