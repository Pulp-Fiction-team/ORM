package client.entity;

import jar.annotation.Column;
import jar.annotation.Id;
import jar.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "entity")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleEntity {
    @Id
    @Column(name = "name")
    private String entityName;

    @Column(name = "age")
    private Integer age;
}
