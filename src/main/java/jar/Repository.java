package jar;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface Repository<ID, T> {

    List<T> findAll() throws SQLException;

    Optional<T> findById(ID id) throws SQLException;

    T save(T entity);

    Integer delete(ID id);

    Class<T> getEntityClass();
    Class<ID> getIDClass();
}
