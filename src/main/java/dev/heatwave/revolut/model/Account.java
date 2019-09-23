package dev.heatwave.revolut.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "Account")
public class Account {
    @Id
    @GeneratedValue
    private Long accountId;

    private String accountHolderName;
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    private Account(Long accountId, String accountHolderName, BigDecimal balance, Currency currency) {
        this.accountId = accountId;
        this.accountHolderName = accountHolderName;
        this.currency = currency;
        if (balance != null) {
            this.balance = balance.setScale(2, RoundingMode.UNNECESSARY);
        }
    }

    public Account() {
    }

    public Long getAccountId() {
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
        private Long accountId;
        private String accountHolderName;
        private BigDecimal balance;
        private Currency currency;

        public static AccountBuilder builder() {
            return new AccountBuilder();
        }

        public AccountBuilder accountId(Long accountId) {
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
