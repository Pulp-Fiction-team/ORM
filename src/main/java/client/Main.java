package client;

import client.entity.SimpleEntity;
import jar.DatabaseMetadataHolder;
import jar.ORMInitializerDefault;
import jar.Repository;
import jar.builder.database.DataBaseMetadataBuilder;
import jar.builder.database.DataBaseMetadataBuilderImpl;
import jar.configuration.DatabaseConfiguration;
import jar.connection.ConnectionHolder;
import jar.model.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws SQLException {

        Properties additionalProperties = new Properties();
        additionalProperties.setProperty("ssl","false");

        String url = "jdbc:postgresql://localhost/orm";
        String packagePath = "client.entity";

        DatabaseConfiguration databaseConfiguration = DatabaseConfiguration.builder()
                .url(url)
                .databaseName("orm")
                .user("postgres")
                .password("thinks")
                .additionalProperties(additionalProperties)
                .build();

        ORMInitializerDefault ormInitializer = new ORMInitializerDefault();
//        ormInitializer.initialize(packagePath, databaseConfiguration);


        ///////////////////////////////////////////////////////////////////////////////

        Repository<String, SimpleEntity> repository = new SimpleEntityRepository();
        Optional<SimpleEntity> optionalSimpleEntity = repository.findById("danik");

        if (optionalSimpleEntity.isPresent()) {
            System.out.println(optionalSimpleEntity.get());
        } else {
            System.out.println("Entity not found");
        }

        List<SimpleEntity> entities = repository.findAll();
        System.out.println(entities);
    }
}
