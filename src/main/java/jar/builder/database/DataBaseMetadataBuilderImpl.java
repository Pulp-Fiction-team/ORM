package jar.builder.database;

import jar.annotation.Column;
import jar.annotation.Id;
import jar.annotation.Table;
import jar.configuration.DatabaseConfiguration;
import jar.model.Database;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class DataBaseMetadataBuilderImpl implements DataBaseMetadataBuilder {

    private final DatabaseConfiguration databaseConfiguration;

    public DataBaseMetadataBuilderImpl(DatabaseConfiguration databaseConfiguration) {
        this.databaseConfiguration = databaseConfiguration;
    }

    @Override
    public Database build(String packagePath) {
        Set<Class<?>> allClasses = getEntityClasses(packagePath);
        List<jar.model.Table> tables = allClasses.stream()
                .filter(clazz -> {
                    Table[] tablesAnnotations = clazz.getDeclaredAnnotationsByType(Table.class);
                    return tablesAnnotations.length > 0;
                })
                .map(
                        clazz -> {
                            Table[] tablesAnnotations = clazz.getDeclaredAnnotationsByType(Table.class);
                            Table tableAnnotation = tablesAnnotations[0];
                            String tableName = tableAnnotation.name();

                            Field[] fields = clazz.getDeclaredFields();

                            List<jar.model.Column> columns = Arrays.stream(fields)
                                    .filter(
                                            field -> {
                                                Column[] columnAnnotations = field.getAnnotationsByType(Column.class);
                                                return columnAnnotations.length > 0;
                                            }
                                    )
                                    .map(
                                            field -> {
                                                Column[] columnAnnotations = field.getAnnotationsByType(Column.class);
                                                Column column = columnAnnotations[0];

                                                Id[] idAnnotations = field.getAnnotationsByType(Id.class);
                                                boolean isIdColumn = idAnnotations.length > 0;

                                                return jar.model.Column.builder()
                                                        .isId(isIdColumn)
                                                        .columnName(column.name())
                                                        .classField(field.getName())
                                                        .clazz(field.getType())
                                                        .build();
                                            }
                                    )
                                    .collect(Collectors.toList());

                            Optional<jar.model.Column> idColumnOptional = columns.stream()
                                    .filter(jar.model.Column::isId)
                                    .findAny();
                            if (!idColumnOptional.isPresent()) {
                                throw new RuntimeException(
                                        String.format(
                                                "Id column not exists in table %s", tableName
                                        )
                                );
                            }

                            jar.model.Column idColumn = idColumnOptional.get();

                            return jar.model.Table.builder()
                                    .idColumn(idColumn)
                                    .name(tableName)
                                    .columns(columns)
                                    .tableType(clazz)
                                    .build();
                        }
                )
                .collect(Collectors.toList());
        return Database.builder()
                .name(databaseConfiguration.getDatabaseName())
                .tables(tables)
                .build();
    }

    public abstract Set<Class<?>> getEntityClasses(String packagePath);
//        Reflections reflections = new Reflections(new ConfigurationBuilder()
//                .setScanners(new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner())
//                .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
//                .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(packagePath))));
//        Set<Class<?>> allClasses =
//                reflections.getSubTypesOf(Object.class);
//        return allClasses;
//    }
}
