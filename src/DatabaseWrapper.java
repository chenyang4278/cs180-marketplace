import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DatabaseWrapper
 * <p>
 * Wraps the Database class with functions that make it easier
 * to interact with the database and not worry about thread-safety.
 *
 * @author Ayden Cline
 * @version 3/31/25
 */
public class DatabaseWrapper implements IDatabaseWrapper {
    static private DatabaseWrapper instance;
    static private final Object staticLock = new Object();

    private final Database idDb;
    static private final String[] idColumns = new String[] {
        "cls",
        "id"
    };

    private final ArrayList<Database> databases;
    private final Object lock = new Object();

    private DatabaseWrapper() {
        idDb = new Database("id.csv", idColumns);
        databases = new ArrayList<Database>();
    }

    private ArrayList<String[]> getRows(Database db, String column, String value) {
        try {
            return db.get(column, value);
        } catch (DatabaseNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private ArrayList<String[]> requireRows(Database db, String column, String value, String errorMsg) throws RowNotFoundException {
        ArrayList<String[]> rows = getRows(db, column, value);
        if (rows.isEmpty()) {
            throw new RowNotFoundException(errorMsg);
        }

        return rows;
    }

    private <T extends Serializable> int getNextId(Class<T> cls) throws DatabaseWriteException {
        String clsName = cls.getSimpleName();

        ArrayList<String[]> ids = getRows(idDb, "cls", clsName);
        if (ids.isEmpty()) {
            try {
                idDb.write(new String[] { clsName, "1" });
            } catch (DatabaseNotFoundException e) {
                throw new DatabaseWriteException("Failed to create ids file");
            }

            return 1;
        }

        int nextId = Integer.parseInt(ids.get(0)[1]) + 1;

        try {
            idDb.update("cls", clsName, "id", String.valueOf(nextId));
        } catch (DatabaseNotFoundException e) {
            throw new DatabaseWriteException("Failed to update ids row");
        }

        return nextId;
    }

    private <T extends Serializable> Database getDbFor(Class<T> cls) {
        String dbName = cls.getSimpleName() + ".csv";
        
        synchronized (lock) {
            for (Database db : databases) {
                if (db.getFilename().equals(dbName)) {
                    return db;
                }
            }

            Database db = new Database(dbName, Serializable.getColumns(cls));
            databases.add(db);
            return db;
        }
    }

    /**
     * Get an instance of a class from one of its columns
     *
     * @param cls the class to get an instance of
     * @param column the column name
     * @param value the value of the column
     * @return an instance of T
     * @param <T> type that extends Serializable
     * @throws RowNotFoundException thrown if a matching row is not found
     */
    public <T extends Serializable> T getByColumn(Class<T> cls, String column, String value) throws RowNotFoundException {
        Database db = getDbFor(cls);
        ArrayList<String[]> rows = requireRows(db, column, value, cls.getSimpleName() + " not found");
        String[] row = rows.get(0);
        return Serializable.fromRow(cls, row);
    }

    /**
     * Get a list of instances of a class, filtering by a column
     *
     * @param cls the class to get instances of
     * @param column the column name
     * @param value the value of the column
     * @return a list of instances according to the filter
     * @param <T> type that extends Serializable
     */
    public <T extends Serializable> List<T> filterByColumn(Class<T> cls, String column, String value) {
        Database db = getDbFor(cls);
        ArrayList<String[]> rows = getRows(db, column, value);
        return rows.stream().map(row -> Serializable.fromRow(cls, row)).collect(Collectors.toList());
    }

    /**
     * Get an instance of a class from an id value
     *
     * @param cls the class to get an instance of
     * @param id the id value
     * @return an instance of T
     * @param <T> type that extends Serializable
     * @throws RowNotFoundException thrown if the id is not found
     */
    public <T extends Serializable> T getById(Class<T> cls, int id) throws RowNotFoundException {
        return getByColumn(cls, "id", String.valueOf(id));
    }

    /**
     * Save an object. If its id is 0, then it's assumed
     * that the object does not yet exist in the database,
     * so a new row is added.
     *
     * @param obj the object to save
     * @param <T> type that extends Serializable
     * @throws DatabaseWriteException thrown when failing to write for whatever reason
     */
    public <T extends Serializable> void save(T obj) throws DatabaseWriteException {
        var cls = obj.getClass();
        Database db = getDbFor(cls);

        try {
            // object has not yet been saved to the db
            if (obj.getId() == 0) {
                obj.setId(getNextId(cls));
                db.write(obj.asRow());
                return;
            }

            db.update("id", String.valueOf(obj.getId()), obj.asRow());
        } catch (DatabaseNotFoundException e) {
            throw new DatabaseWriteException("Failed to save");
        }
    }

    /**
     * Delete an object
     *
     * @param obj the object to delete
     * @param <T> type that extends Serializable
     * @throws DatabaseWriteException thrown when failing to delete for whatever reason
     */
    public <T extends Serializable> void delete(T obj) throws DatabaseWriteException {
        Class<? extends Serializable> cls = obj.getClass();
        Database db = getDbFor(cls);

        try {
            db.delete("id", String.valueOf(obj.getId()));
        } catch (DatabaseNotFoundException e) {
            throw new DatabaseWriteException("Failed to delete");
        }
    }

    /**
     * @return Global DatabaseWrapper instance
     */
    static public DatabaseWrapper get() {
        synchronized (staticLock) {
            if (instance == null) {
                instance = new DatabaseWrapper();
            }
        }

        return instance;
    }
}
