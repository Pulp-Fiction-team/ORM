package jar.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Column {
    private boolean isId = false;
    private String columnName;
    private String classField;
    private Class<?> clazz;
}
