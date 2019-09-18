package dev.heatwave.revolut.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "Account")
public class Account {
    @Id
    @GeneratedValue
    private long accountId;

    private String accountHolderName;
    private BigDecimal balance;
    private Currency currency;


    public Account(String accountHolderName, BigDecimal balance, Currency currency) {
        this.accountHolderName = accountHolderName;
        this.balance = balance;
        this.currency = currency;
    }

    private Account(long accountId, String accountHolderName, BigDecimal balance, Currency currency) {
        this.accountId = accountId;
        this.accountHolderName = accountHolderName;
        this.balance = balance;
        this.currency = currency;
    }

    public Account() {
    }

    public long getAccountId() {
        return accountId;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Currency getCurrency() {
        return currency;
    }

    public AccountBuilder toBuilder() {
        return AccountBuilder.builder()
                .accountId(this.accountId)
                .accountHolderName(this.accountHolderName)
                .balance(this.balance)
                .currency(this.currency);
    }

    public static class AccountBuilder {
        private long accountId;
        private String accountHolderName;
        private BigDecimal balance;
        private Currency currency;

        public static AccountBuilder builder() {
            return new AccountBuilder();
        }

        public AccountBuilder accountId(long accountId) {
            this.accountId = accountId;
            return this;
        }

        public AccountBuilder accountHolderName(String accountHolderName) {
            this.accountHolderName = accountHolderName;
            return this;
        }

        public AccountBuilder balance(BigDecimal balance) {
            this.balance = balance;
            return this;
        }

        public AccountBuilder currency(Currency currency) {
            this.currency = currency;
            return this;
        }

        public Account build() {
            return new Account(accountId, accountHolderName, balance, currency);
        }
    }
}
