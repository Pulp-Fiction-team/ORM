package jar.builder.database;

import jar.model.Database;

public interface DataBaseMetadataBuilder {
    Database build(String packagePath);
}
