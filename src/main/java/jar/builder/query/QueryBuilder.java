package jar.builder.query;

import jar.model.Table;

public interface QueryBuilder<ID, T> {
    String findByIdQuery(Table table, ID id, Class<ID> idClass, Class<T> entityClass);

    String findAllQuery(Table table, Class<T> entityClass);

    String saveQuery(Table table, Object entity, Class<T> tClass);
}