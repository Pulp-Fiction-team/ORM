package jar.builder.query;

import jar.model.Column;
import jar.model.Table;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryBuilderImpl<ID, T> implements QueryBuilder<ID, T> {

    public static final String COLUMN_NAMES_PLACEHOLDER = "{columnNames}";
    public static final String TABLE_NAME_PLACEHOLDER = "{tableName}";
    public static final String ID_PLACEHOLDER = "{id}";
    public static final String ID_VALUE_PLACEHOLDER = "{idValue}";
    public static final String SELECT_BY_ID_TEMPLATE = "select " + COLUMN_NAMES_PLACEHOLDER
            + " from " + TABLE_NAME_PLACEHOLDER + " where " + ID_PLACEHOLDER + " = " + ID_VALUE_PLACEHOLDER;
    public static final String SELECT_ALL_TEMPLATE = "select " + COLUMN_NAMES_PLACEHOLDER
            + " from " + TABLE_NAME_PLACEHOLDER;
    public static final String QUOTES_TEMPLATE = "'%s'";

    private final Map<Class<?>, IDValueFunction<ID>> idValueFunctionMap;

    public QueryBuilderImpl() {
        this.idValueFunctionMap = idValueFunctionMap();
    }

    @Override
    public String findByIdQuery(Table table, ID id, Class<ID> idClass, Class<T> entityClass) {
        validate(table, entityClass);

        Column idColumn = table.getIdColumn();
        String idColumnName = idColumn.getColumnName();
        String idValue = idValue(idColumn, id, idClass);

        List<Column> columns = table.getColumns();
        String columnNamesList = columns.stream()
                .map(Column::getColumnName)
                .collect(Collectors.joining(","));
        String tableName = table.getName();
        return SELECT_BY_ID_TEMPLATE
                .replace(COLUMN_NAMES_PLACEHOLDER, columnNamesList)
                .replace(TABLE_NAME_PLACEHOLDER, tableName)
                .replace(ID_PLACEHOLDER, idColumnName)
                .replace(ID_VALUE_PLACEHOLDER, idValue);
    }

    @Override
    public String findAllQuery(Table table, Class<T> entityClass) {
        validate(table, entityClass);

        List<Column> columns = table.getColumns();
        String columnNamesList = columns.stream()
                .map(Column::getColumnName)
                .collect(Collectors.joining(","));
        String tableName = table.getName();
        return SELECT_ALL_TEMPLATE
                .replace(COLUMN_NAMES_PLACEHOLDER, columnNamesList)
                .replace(TABLE_NAME_PLACEHOLDER, tableName);
    }

    private void validate(Table table, Class<T> entityClass) {
        Class<?> tableType = table.getTableType();
        if (!tableType.equals(entityClass)) {
            throw new RuntimeException("Entity class does not match table defined class");
        }
    }

    @Override
    public String saveQuery(Table table, Object entity, Class<T> tClass) {
        return null;
    }

    private interface IDValueFunction<ID> {

        String apply(Column column, ID id, Class<ID> idClass);
    }

    private String idValue(Column column, ID id, Class<ID> idClass) {
        Class<?> fieldType = column.getClazz();
        if (idValueFunctionMap.containsKey(fieldType)) {
            IDValueFunction<ID> idValueFunction = idValueFunctionMap.get(fieldType);
            return idValueFunction.apply(column, id, idClass);
        }

        return id.toString();
    }

    private Map<Class<?>, IDValueFunction<ID>> idValueFunctionMap() {
        Map<Class<?>, IDValueFunction<ID>> map = new HashMap<>();
        map.put(String.class, ((column, id, idClass) -> String.format(QUOTES_TEMPLATE, id.toString())));
        return map;
    }

}
