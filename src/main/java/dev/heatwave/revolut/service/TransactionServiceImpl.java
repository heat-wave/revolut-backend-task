package dev.heatwave.revolut.service;

import dev.heatwave.revolut.model.account.Account;
import dev.heatwave.revolut.model.transaction.Transaction;
import dev.heatwave.revolut.persistence.MockPersistenceManager;

import java.util.List;
import java.util.Optional;

public class TransactionServiceImpl implements TransactionService {
    private AccountService accountService;
    private MockPersistenceManager persistenceManager;

    @Override
    public Transaction createTransaction(Transaction transaction) {
        //begin transaction
        Optional<Account> sender = accountService.getAccountById(transaction.getSenderId());
        Optional<Account> recipient = accountService.getAccountById(transaction.getRecipientId());
        if (!sender.isPresent()) {
            //bad request: wrong sender id
        }
        if (!recipient.isPresent()) {
            //bad request: wrong recipient id
        }
        //bad request if currencies mismatch
        if (sender.get().getBalance().compareTo(transaction.getAmount()) <= 0) {
            //bad request: insufficient funds
        }

        Transaction result = new Transaction(sender.get().getAccountId(), recipient.get().getAccountId(), transaction.getAmount());
        persistenceManager.save(result);

        //end transaction
        return null;
    }

    @Override
    public Optional<Transaction> getTransactionById(long transactionId) {
        return Optional.empty();
    }

    @Override
    public List<Transaction> getTransactionsByUserId(long userId) {
        return null;
    }
}
