package dev.heatwave.revolut.service;

import dev.heatwave.revolut.exception.ForbiddenOperationException;
import dev.heatwave.revolut.model.Account;
import dev.heatwave.revolut.model.Currency;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.math.BigDecimal;
import java.util.Optional;

class AccountServiceImplTest {

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountServiceImpl();
    }

    @Test
    void createAccount__with_valid_parameters__succeeds() {
        //given
        final Account accountToPersist = Account.AccountBuilder.builder()
                .balance(BigDecimal.TEN)
                .accountHolderName("customer")
                .currency(Currency.GBP)
                .build();

        //when
        final Account result = accountService.createAccount(accountToPersist);

        //then
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getAccountId());
        Assertions.assertEquals(0, accountToPersist.getBalance().compareTo(result.getBalance()));
        Assertions.assertEquals(accountToPersist.getCurrency(), result.getCurrency());
        Assertions.assertEquals(accountToPersist.getAccountHolderName(), result.getAccountHolderName());
    }

    @Test
    void createAccount__with_negative_balance__fails() {
        //given
        final Account accountToPersist = Account.AccountBuilder.builder()
                .balance(BigDecimal.TEN.negate())
                .accountHolderName("customer")
                .currency(Currency.GBP)
                .build();

        //when
        Executable operation = () -> accountService.createAccount(accountToPersist);

        //then
        Assertions.assertThrows(ForbiddenOperationException.class, operation);
    }

    @Test
    void createAccount__without_currency_specified__fails() {
        //given
        final Account accountToPersist = Account.AccountBuilder.builder()
                .balance(BigDecimal.TEN)
                .accountHolderName("customer")
                .build();

        //when
        Executable operation = () -> accountService.createAccount(accountToPersist);

        //then
        Assertions.assertThrows(ForbiddenOperationException.class, operation);
    }

    @Test
    void createAccount__with_null_account__fails() {
        //given
        final Account accountToPersist = null;

        //when
        Executable operation = () -> accountService.createAccount(accountToPersist);

        //then
        Assertions.assertThrows(ForbiddenOperationException.class, operation);
    }


    @Test
    void getAccountById__with_valid_id__returns_account() {
        //given
        final Account accountToPersist = Account.AccountBuilder.builder()
                .balance(BigDecimal.TEN)
                .accountHolderName("customer")
                .currency(Currency.GBP)
                .build();
        final Account persistedAccount = accountService.createAccount(accountToPersist);

        //when
        final Optional<Account> retrievedAccount = accountService.getAccountById(persistedAccount.getAccountId());

        //then
        Assertions.assertTrue(retrievedAccount.isPresent());
        Assertions.assertEquals(0, persistedAccount.getBalance().compareTo(retrievedAccount.get().getBalance()));
        Assertions.assertEquals(persistedAccount.getAccountHolderName(), retrievedAccount.get().getAccountHolderName());
        Assertions.assertEquals(persistedAccount.getCurrency(), retrievedAccount.get().getCurrency());
        Assertions.assertEquals(persistedAccount.getAccountId(), retrievedAccount.get().getAccountId());
    }

    @Test
    void getAccountById__with_nonexistent_id__returns_empty() {
        //when
        final Optional<Account> retrievedAccount = accountService.getAccountById(100L);

        //then
        Assertions.assertFalse(retrievedAccount.isPresent());
    }

    @Test
    void getAccountById__with_null_id__returns_empty() {
        //when
        final Optional<Account> retrievedAccount = accountService.getAccountById(null);

        //then
        Assertions.assertFalse(retrievedAccount.isPresent());
    }
}