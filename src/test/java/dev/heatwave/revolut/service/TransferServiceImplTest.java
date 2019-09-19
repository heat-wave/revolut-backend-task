package dev.heatwave.revolut.service;

import dev.heatwave.revolut.exception.ForbiddenOperationException;
import dev.heatwave.revolut.exception.ResourceNotFoundException;
import dev.heatwave.revolut.model.Account;
import dev.heatwave.revolut.model.Currency;
import dev.heatwave.revolut.model.Transfer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

class TransferServiceImplTest {

    private TransferService transferService;
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountServiceImpl();
        transferService = new TransferServiceImpl(accountService);
    }

    @Test
    void createTransfer__with_valid_parameters__succeeds() {
        //given
        final List<Account> accountsToPersist = Collections.nCopies(2, Account.AccountBuilder.builder()
                .balance(BigDecimal.TEN)
                .accountHolderName("customer")
                .currency(Currency.GBP)
                .build()
        );
        accountsToPersist.forEach(account -> accountService.createAccount(account));

        final Transfer transfer = new Transfer(1L, 2L, BigDecimal.ONE);

        //when
        final Transfer result = transferService.createTransfer(transfer);

        //then
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getTransferId());
        Assertions.assertEquals(0, result.getAmount().compareTo(transfer.getAmount()));
        Assertions.assertEquals(transfer.getSenderId(), result.getSenderId());
        Assertions.assertEquals(transfer.getRecipientId(), result.getRecipientId());
    }

    @Test
    void createTransfer__without_sender__fails() {
        //given
        final List<Account> accountsToPersist = Collections.nCopies(2, Account.AccountBuilder.builder()
                .balance(BigDecimal.TEN)
                .accountHolderName("customer")
                .currency(Currency.GBP)
                .build()
        );
        accountsToPersist.forEach(account -> accountService.createAccount(account));

        final Transfer transfer = new Transfer(null, 2L, BigDecimal.ONE);

        //when
        final Executable operation = () -> transferService.createTransfer(transfer);

        //then
        Assertions.assertThrows(ResourceNotFoundException.class, operation);
    }

    @Test
    void createTransfer__without_recipient__fails() {
        //given
        final List<Account> accountsToPersist = Collections.nCopies(2, Account.AccountBuilder.builder()
                .balance(BigDecimal.TEN)
                .accountHolderName("customer")
                .currency(Currency.GBP)
                .build()
        );
        accountsToPersist.forEach(account -> accountService.createAccount(account));

        final Transfer transfer = new Transfer(1L, null, BigDecimal.ONE);

        //when
        final Executable operation = () -> transferService.createTransfer(transfer);

        //then
        Assertions.assertThrows(ResourceNotFoundException.class, operation);
    }

    @Test
    void createTransfer__without_amount__fails() {
        //given
        final List<Account> accountsToPersist = Collections.nCopies(2, Account.AccountBuilder.builder()
                .balance(BigDecimal.TEN)
                .accountHolderName("customer")
                .currency(Currency.GBP)
                .build()
        );
        accountsToPersist.forEach(account -> accountService.createAccount(account));

        final Transfer transfer = new Transfer(1L, 2L, null);

        //when
        final Executable operation = () -> transferService.createTransfer(transfer);

        //then
        Assertions.assertThrows(ForbiddenOperationException.class, operation);
    }

    @Test
    void createTransfer__with_negative_amount__fails() {
        //given
        final List<Account> accountsToPersist = Collections.nCopies(2, Account.AccountBuilder.builder()
                .balance(BigDecimal.TEN)
                .accountHolderName("customer")
                .currency(Currency.GBP)
                .build()
        );
        accountsToPersist.forEach(account -> accountService.createAccount(account));

        final Transfer transfer = new Transfer(1L, 2L, BigDecimal.ONE.negate());

        //when
        final Executable operation = () -> transferService.createTransfer(transfer);

        //then
        Assertions.assertThrows(ForbiddenOperationException.class, operation);
    }


    @Test
    void getTransferById__with_valid_id__returns_transfer() {
        //given
        final List<Account> accountsToPersist = Collections.nCopies(2, Account.AccountBuilder.builder()
                .balance(BigDecimal.TEN)
                .accountHolderName("customer")
                .currency(Currency.GBP)
                .build()
        );
        accountsToPersist.forEach(account -> accountService.createAccount(account));

        final Transfer persistedTransfer = transferService.createTransfer(new Transfer(1L, 2L, BigDecimal.ONE));

        //when
        Optional<Transfer> retrievedTransfer = transferService.getTransferById(persistedTransfer.getTransferId());

        //then
        Assertions.assertTrue(retrievedTransfer.isPresent());
        Assertions.assertEquals(persistedTransfer.getTransferId(), retrievedTransfer.get().getTransferId());
        Assertions.assertEquals(0, persistedTransfer.getAmount().compareTo(retrievedTransfer.get().getAmount()));
        Assertions.assertEquals(persistedTransfer.getSenderId(), retrievedTransfer.get().getSenderId());
        Assertions.assertEquals(persistedTransfer.getRecipientId(), retrievedTransfer.get().getRecipientId());
    }

    @Test
    void getTransferById__with_nonexistent_id__returns_empty() {
        //when
        Optional<Transfer> retrievedTransfer = transferService.getTransferById(100L);

        //then
        Assertions.assertFalse(retrievedTransfer.isPresent());
    }

    @Test
    void getTransferById__with_null_id__returns_empty() {
        //when
        Optional<Transfer> retrievedTransfer = transferService.getTransferById(null);

        //then
        Assertions.assertFalse(retrievedTransfer.isPresent());
    }


    @Test
    void getTransfersByAccountId__only_returns_that_accounts_transfers() {
        //given
        final List<Account> accountsToPersist = Collections.nCopies(3, Account.AccountBuilder.builder()
                .balance(BigDecimal.TEN)
                .accountHolderName("customer")
                .currency(Currency.GBP)
                .build()
        );
        accountsToPersist.forEach(account -> accountService.createAccount(account));

        final List<Transfer> transfersToPersist = Arrays.asList(
                new Transfer(1L, 2L, BigDecimal.ONE),
                new Transfer(2L, 1L, BigDecimal.ONE),
                new Transfer(1L, 3L, BigDecimal.ONE)
        );
        transfersToPersist.forEach(transfer -> transferService.createTransfer(transfer));

        //when
        List<Transfer> selectedTransfers = transferService.getTransfersByAccountId(2L);

        //then
        Assertions.assertEquals(2, selectedTransfers.size());
        selectedTransfers.forEach(transfer ->
                Assertions.assertTrue(transfer.getRecipientId().equals(2L) || transfer.getSenderId().equals(2L))
        );
    }

    @Test
    void getTransfersByAccountId__with_nonexistent_id__returns_empty_list() {
        //when
        List<Transfer> selectedTransfers = transferService.getTransfersByAccountId(100L);

        //then
        Assertions.assertEquals(0, selectedTransfers.size());
    }

    @Test
    void getTransfersByAccountId__with_null_id__returns_empty_list() {
        //when
        List<Transfer> selectedTransfers = transferService.getTransfersByAccountId(null);

        //then
        Assertions.assertEquals(0, selectedTransfers.size());
    }
}