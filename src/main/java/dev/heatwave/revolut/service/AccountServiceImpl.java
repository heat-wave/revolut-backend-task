package dev.heatwave.revolut.service;

import dev.heatwave.revolut.model.account.Account;
import dev.heatwave.revolut.persistence.MockPersistenceManager;

import java.util.Optional;

public class AccountServiceImpl implements AccountService {
    private MockPersistenceManager persistenceManager;

    public AccountServiceImpl(MockPersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }

    @Override
    public Account createAccount(Account account) {
        return persistenceManager.save(account);
    }

    @Override
    public Optional<Account> getAccountById(long accountId) {
        return Optional.empty();
    }
}
