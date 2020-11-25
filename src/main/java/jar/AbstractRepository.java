package jar;

import jar.builder.entity.EntityBuilder;
import jar.builder.entity.EntityBuilderImpl;
import jar.builder.query.QueryBuilder;
import jar.builder.query.QueryBuilderImpl;
import jar.connection.ConnectionHolder;
import jar.model.Database;
import jar.model.Table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractRepository<ID, T> implements Repository<ID, T> {

    private final QueryBuilder<ID, T> queryBuilder;
    private final EntityBuilder<T> entityBuilder;

    public AbstractRepository() {
        this.queryBuilder = new QueryBuilderImpl<>();
        this.entityBuilder = new EntityBuilderImpl<>();
    }

    @Override
    public List<T> findAll() throws SQLException {
        Table table = getTable();
        QueryBuilder<ID, T> tQueryBuilder = getQueryBuilder();
        Class<T> entityClass = getEntityClass();
        String selectQuery = tQueryBuilder.findAllQuery(table, entityClass);

        Connection connection = ConnectionHolder.getInstance().getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
        ResultSet resultSet = preparedStatement.executeQuery();
        return entityBuilder.buildFromFindAllResultSet(table, resultSet, entityClass);
    }

    @Override
    public Optional<T> findById(ID id) throws SQLException {
        Table table = getTable();
        QueryBuilder<ID, T> tQueryBuilder = getQueryBuilder();
        Class<T> entityClass = getEntityClass();
        Class<ID> idClass = getIDClass();
        String selectQuery = tQueryBuilder.findByIdQuery(table, id, idClass, entityClass);

        Connection connection = ConnectionHolder.getInstance().getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
        ResultSet resultSet = preparedStatement.executeQuery();
        return entityBuilder.buildFromFindByIdResultSet(table, resultSet, entityClass);
    }

    @Override
    public T save(T entity) {
        throw new RuntimeException("Operation not supported");
    }

    @Override
    public Integer delete(ID id) {
        throw new RuntimeException("Operation not supported");
    }

    private QueryBuilder<ID, T> getQueryBuilder() {
        return queryBuilder;
    }

    private Table getTable() {
        DatabaseMetadataHolder databaseMetadataHolder = DatabaseMetadataHolder.getInstance();
        Database database = databaseMetadataHolder.getDatabase();
        if (Objects.isNull(database)) {
            throw new IllegalArgumentException("Database object is null");
        }
        Optional<Table> tableOptional = database.getTables().stream()
                .filter(table -> {
                    Class<?> tableType = table.getTableType();
                    Class<T> entityClass = getEntityClass();
                    return tableType.equals(entityClass);
                })
                .findAny();
        if (!tableOptional.isPresent()) {
            throw new RuntimeException(
                    String.format(
                            "Entity with type [%s] is not exist.",
                            getEntityClass()
                    )
            );
        }

        return tableOptional.get();
    }
}
