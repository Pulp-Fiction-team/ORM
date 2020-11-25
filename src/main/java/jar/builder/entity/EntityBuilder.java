package jar.builder.entity;

import jar.model.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface EntityBuilder<T> {
    Optional<T> buildFromFindByIdResultSet(Table table, ResultSet resultSet, Class<T> entityType) throws SQLException;

    List<T> buildFromFindAllResultSet(Table table, ResultSet resultSet, Class<T> entityType) throws SQLException;
}
