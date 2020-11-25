package jar;

import jar.configuration.DatabaseConfiguration;

import java.sql.SQLException;

public interface ORMInitializer {
    void initialize(String pacakgePath, DatabaseConfiguration configuration) throws SQLException;
}
