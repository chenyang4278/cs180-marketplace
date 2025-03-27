public class DatabaseWrapper implements IDatabaseWrapper {
    static private DatabaseWrapper instance;

    private Database userDb;
    private Database listingDb;

    private DatabaseWrapper() {
        userDb = new BaseDatabase("users.csv");
        listingDb = new BaseDatabase("listings.csv");
    }

    /**
     * @return Global DatabaseWrapper instance
     */
    static public DatabaseWrapper get() {
        if (instance == null) {
            instance = new DatabaseWrapper();
        }

        return instance;
    }
}
