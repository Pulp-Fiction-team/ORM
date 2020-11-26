package jar.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatabaseConfiguration {
    public static final String USER = "user";
    public static final String PASSWORD = "password";
    private String databaseName;
    private String url;
    private String user;
    private String password;
    private List<ClassLoader> classLoadersList = new LinkedList<>();

    private Properties additionalProperties;

    public Properties asProperties() {
        Properties properties = new Properties();
        additionalProperties.stringPropertyNames().forEach(
                name -> {
                    String value = additionalProperties.getProperty(name);
                    properties.setProperty(name, value);
                }
        );
        properties.setProperty(USER, user);
        properties.setProperty(PASSWORD, password);
        return properties;
    }
}
