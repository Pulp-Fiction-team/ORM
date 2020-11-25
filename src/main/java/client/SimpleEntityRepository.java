package client;

import client.entity.SimpleEntity;
import jar.AbstractRepository;

public class SimpleEntityRepository extends AbstractRepository<String, SimpleEntity> {
    @Override
    public Class<SimpleEntity> getEntityClass() {
        return SimpleEntity.class;
    }

    @Override
    public Class<String> getIDClass() {
        return String.class;
    }
}
