import java.util.ArrayList;
import java.util.List;

public class DatabaseWrapper implements IDatabaseWrapper {
    static private IDatabaseWrapper instance;
    static private final Object staticLock = new Object();

    private final IDatabase idDb;
    static private final String[] idColumns = new String[] {
        "cls",
        "id"
    };

    private final ArrayList<IDatabase> databases;

    private final Object lock = new Object();

    private DatabaseWrapper() {
        idDb = new Database("id.csv", idColumns);
        databases = new ArrayList<IDatabase>();
    }

    private int getIndex(String[] columns, String key) {
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].equals(key)) {
                return i;
            }
        }

        return -1;
    }

    private ArrayList<String[]> getRows(IDatabase db, String column, String value) {
        try {
            return db.get(column, value);
        } catch (DatabaseNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private ArrayList<String[]> requireRows(IDatabase db, String column, String value, String errorMsg) throws RowNotFoundException {
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

        int nextId = Integer.parseInt(ids.getFirst()[1]) + 1;

        try {
            idDb.update("cls", clsName, "id", String.valueOf(nextId));
        } catch (DatabaseNotFoundException e) {
            throw new DatabaseWriteException("Failed to update ids row");
        }

        return nextId;
    }

    private <T extends Serializable> IDatabase getDbFor(Class<T> cls) {
        String dbName = cls.getSimpleName() + ".csv";
        for (IDatabase db : databases) {
            if (db.getFilename().equals(dbName)) {
                return db;
            }
        }

        IDatabase db = new Database(dbName, Serializable.getColumns(cls));
        databases.add(db);
        return db;
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
        synchronized (lock) {
            IDatabase db = getDbFor(cls);
            ArrayList<String[]> rows = requireRows(db, column, value, cls.getSimpleName() + " not found");
            String[] row = rows.getFirst();
            return Serializable.fromRow(cls, row);
        }
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
        synchronized (lock) {
            IDatabase db = getDbFor(cls);
            ArrayList<String[]> rows = getRows(db, column, value);
            return rows.stream().map(row -> Serializable.fromRow(cls, row)).toList();
        }
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
        synchronized (lock) {
            var cls = obj.getClass();
            IDatabase db = getDbFor(cls);

            try {
                // object has not yet been saved to the db
                if (obj.getId() == 0) {
                    obj.setId(getNextId(cls));
                    db.write(obj.asRow());
                    return;
                }

                // update entire row here (method doesn't exist yet)
                db.update("id", String.valueOf(obj.getId()), obj.asRow());
            } catch (DatabaseNotFoundException e) {
                throw new DatabaseWriteException("Failed to save");
            }
        }
    }

    /**
     * @return Global DatabaseWrapper instance
     */
    static public IDatabaseWrapper get() {
        synchronized (staticLock) {
            if (instance == null) {
                instance = new DatabaseWrapper();
            }

            return instance;
        }
    }
}
