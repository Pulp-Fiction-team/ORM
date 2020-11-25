package jar.connection;

import java.sql.Connection;

public class ConnectionHolder {

    private static ConnectionHolder instance;
    private Connection connection;

    private ConnectionHolder() {
    }

    public static ConnectionHolder getInstance() {
        if (instance == null) {
            instance = new ConnectionHolder();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
