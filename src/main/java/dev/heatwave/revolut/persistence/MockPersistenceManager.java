package dev.heatwave.revolut.persistence;

public interface MockPersistenceManager {
    public <T> T save(T object);
    public <T> T getById(long id);
}
