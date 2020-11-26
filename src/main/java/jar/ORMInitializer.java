package jar;

import jar.builder.database.DataBaseMetadataBuilder;
import jar.configuration.DatabaseConfiguration;

import java.sql.SQLException;

public interface ORMInitializer {
    void initialize(String packagePath, DatabaseConfiguration configuration,
                    DataBaseMetadataBuilder dataBaseMetadataBuilder) throws SQLException;
}
