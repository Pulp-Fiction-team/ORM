package jar.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Table {
    private String name;
    private Column idColumn;
    private List<Column> columns;
    private Class<?> tableType;
}
