package dev.heatwave.revolut.persistence;

import dev.heatwave.revolut.util.PropertyResolver;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class PersistenceManager {

    private static EntityManagerFactory entityManagerFactory;

    private static final String url = PropertyResolver.getConfiguration().getString(PropertyResolver.PERSISTENCE_UNIT_NAME);

    public static EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) {
            entityManagerFactory = Persistence.createEntityManagerFactory(url);
        }

        return entityManagerFactory;
    }
}
