package jar;

import jar.model.Database;

public class DatabaseMetadataHolder {
    private static DatabaseMetadataHolder instance;
    private Database database;

    private DatabaseMetadataHolder() {
    }

    public static DatabaseMetadataHolder getInstance() {
        if (instance == null) {
            instance = new DatabaseMetadataHolder();
        }
        return instance;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }
}
