package dev.heatwave.revolut.model.account;

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
    //private Currency currency;


    public Account(String accountHolderName, BigDecimal balance) {
        this.accountHolderName = accountHolderName;
        this.balance = balance;
    }

    private Account(long accountId, String accountHolderName, BigDecimal balance) {
        this.accountId = accountId;
        this.accountHolderName = accountHolderName;
        this.balance = balance;
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

    public AccountBuilder toBuilder() {
        return AccountBuilder.builder()
                .accountId(this.accountId)
                .accountHolderName(this.accountHolderName)
                .balance(this.balance);
    }

    public static class AccountBuilder {
        private long accountId;
        private String accountHolderName;
        private BigDecimal balance;

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

        public Account build() {
            return new Account(accountId, accountHolderName, balance);
        }
    }
}
