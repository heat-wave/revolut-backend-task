package dev.heatwave.revolut.persistence;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class PersistenceManager {
    private static EntityManagerFactory entityManagerFactory;

    public static final String url = "dev.heatwave.revolut"; //TODO: refactor to property

    public static EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) {
            entityManagerFactory = Persistence.createEntityManagerFactory(url);
        }

        return entityManagerFactory;
    }
}
