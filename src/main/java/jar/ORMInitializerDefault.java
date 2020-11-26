package jar;

import jar.builder.database.DataBaseMetadataBuilder;
import jar.builder.database.DataBaseMetadataBuilderImpl;
import jar.configuration.DatabaseConfiguration;
import jar.connection.ConnectionHolder;
import jar.model.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ORMInitializerDefault implements ORMInitializer{
    @Override
    public void initialize(String packagePath, DatabaseConfiguration configuration,
                           DataBaseMetadataBuilder dataBaseMetadataBuilder) throws SQLException {
        Database database = dataBaseMetadataBuilder.build(packagePath);
        DatabaseMetadataHolder databaseMetadataHolder = DatabaseMetadataHolder.getInstance();
        databaseMetadataHolder.setDatabase(database);

        Properties properties = configuration.asProperties();
        Connection connection = DriverManager.getConnection(configuration.getUrl(), properties);
        ConnectionHolder connectionHolder = ConnectionHolder.getInstance();
        connectionHolder.setConnection(connection);
    }
}
