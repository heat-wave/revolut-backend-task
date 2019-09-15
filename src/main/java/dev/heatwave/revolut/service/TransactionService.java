package dev.heatwave.revolut.service;

import dev.heatwave.revolut.model.transaction.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionService {
    public Transaction createTransaction(Transaction transaction);

    public Optional<Transaction> getTransactionById(long transactionId);

    public List<Transaction> getTransactionsByUserId(long userId);
}
