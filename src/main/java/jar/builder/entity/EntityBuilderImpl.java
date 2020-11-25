package jar.builder.entity;

import jar.model.Column;
import jar.model.Table;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EntityBuilderImpl<T> implements EntityBuilder<T> {

    private final Map<Class<?>, FieldSetter<T>> fieldSetterMap;

    public EntityBuilderImpl() {
        this.fieldSetterMap = fieldSetterMap();
    }

    @Override
    public Optional<T> buildFromFindByIdResultSet(Table table, ResultSet resultSet, Class<T> entityType) {
        try {
            if (!resultSet.next()) {
                return Optional.empty();
            }

            T entity = buildByResultItem(table, resultSet, entityType);
            return Optional.of(entity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<T> buildFromFindAllResultSet(Table table, ResultSet resultSet, Class<T> entityType) {
        try {
            List<T> entities = new LinkedList<>();
            while (resultSet.next()) {
                T entity = buildByResultItem(table, resultSet, entityType);
                entities.add(entity);
            }
            return entities;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private T buildByResultItem(Table table, ResultSet resultSet, Class<T> entityType) throws InstantiationException, IllegalAccessException {
        T entity = entityType.newInstance();
        Field[] declaredFields = entityType.getDeclaredFields();
        Map<String, Field> nameFieldMap = Arrays.stream(declaredFields)
                .collect(
                        Collectors.toMap(
                                Field::getName, Function.identity()
                        )
                );

        List<Column> columns = table.getColumns();

        columns.forEach(
                column -> {
                    String classField = column.getClassField();
                    Field field = nameFieldMap.get(classField);
                    field.setAccessible(true);
                    Class<?> fieldType = field.getType();
                    if (!fieldSetterMap.containsKey(fieldType)) {
                        throw new RuntimeException(
                                String.format("Field type [%s] not supported", fieldType.toString())
                        );
                    }

                    FieldSetter<T> fieldSetter = fieldSetterMap.get(fieldType);
                    fieldSetter.apply(resultSet, field, column, entity);
                }
        );
        return entity;
    }

    private interface FieldSetter<T> {
        void apply(ResultSet resultSet, Field field, Column column, T entity);
    }

    private static abstract class AbsractFieldSetter<T, V> implements FieldSetter<T> {
        @Override
        public void apply(ResultSet resultSet, Field field, Column column, T entity) {
            try {
                String columnName = column.getColumnName();
                V value = getValueFromResultSet(resultSet, columnName);
                field.setAccessible(true);
                field.set(entity, value);
            } catch (SQLException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        abstract V getValueFromResultSet(ResultSet resultSet, String columnName) throws SQLException;
    }

    private Map<Class<?>, FieldSetter<T>> fieldSetterMap() {
        Map<Class<?>, FieldSetter<T>> map = new HashMap<>();
        map.put(
                String.class,
                (
                        new AbsractFieldSetter<T, String>() {
                            @Override
                            String getValueFromResultSet(ResultSet resultSet, String columnName) throws SQLException {
                                return resultSet.getString(columnName);
                            }
                        }
                )
        );
        map.put(
                Integer.class,
                (
                        new AbsractFieldSetter<T, Integer>() {
                            @Override
                            Integer getValueFromResultSet(ResultSet resultSet, String columnName) throws SQLException {
                                return resultSet.getInt(columnName);
                            }
                        }
                )
        );
        map.put(
                Long.class,
                (
                        new AbsractFieldSetter<T, Long>() {
                            @Override
                            Long getValueFromResultSet(ResultSet resultSet, String columnName) throws SQLException {
                                return resultSet.getLong(columnName);
                            }
                        }
                )
        );
        return map;
    }
}
