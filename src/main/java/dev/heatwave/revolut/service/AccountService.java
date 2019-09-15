package dev.heatwave.revolut.service;

import dev.heatwave.revolut.model.account.Account;

import java.util.Optional;

public interface AccountService {
    public Account createAccount(Account account);

    public Optional<Account> getAccountById(long accountId);

    //update state

    //update balance?
}
