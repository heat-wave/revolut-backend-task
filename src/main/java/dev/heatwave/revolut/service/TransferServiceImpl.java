package dev.heatwave.revolut.service;

import dev.heatwave.revolut.exception.ResourceNotFoundException;
import dev.heatwave.revolut.exception.ForbiddenOperationException;
import dev.heatwave.revolut.model.Account;
import dev.heatwave.revolut.model.Transfer;
import dev.heatwave.revolut.persistence.PersistenceManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TransferServiceImpl implements TransferService {

    private final AccountService accountService;
    private final EntityManagerFactory entityManagerFactory = PersistenceManager.getEntityManagerFactory();

    public TransferServiceImpl(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public Transfer createTransfer(Transfer transfer) {

        final EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        final Optional<Account> sender = accountService.getAccountById(transfer.getSenderId());
        final Optional<Account> recipient = accountService.getAccountById(transfer.getRecipientId());

        if (!sender.isPresent()) {
            entityManager.getTransaction().rollback();
            throw new ResourceNotFoundException("Sender account with id %d not found", transfer.getSenderId());
        }
        if (!recipient.isPresent()) {
            entityManager.getTransaction().rollback();
            throw new ResourceNotFoundException("Recipient account with id %d not found", transfer.getRecipientId());
        }
        if (!sender.get().getCurrency().equals(recipient.get().getCurrency())) {
            entityManager.getTransaction().rollback();
            throw new ForbiddenOperationException("Currency conversion between accounts is not supported in this API version");
        }
        if (transfer.getAmount() == null || transfer.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            entityManager.getTransaction().rollback();
            throw new ForbiddenOperationException("Transaction amount must be positive");
        }
        if (sender.get().getBalance().compareTo(transfer.getAmount()) < 0) {
            entityManager.getTransaction().rollback();
            throw new ForbiddenOperationException("Insufficient funds in the sender's account");
        }

        final Account updatedSender = sender.get()
                .toBuilder()
                .balance(sender.get().getBalance().subtract(transfer.getAmount()))
                .build();
        final Account updatedRecipient = recipient.get()
                .toBuilder()
                .balance(recipient.get().getBalance().add(transfer.getAmount()))
                .build();
        entityManager.merge(updatedSender);
        entityManager.merge(updatedRecipient);
        Transfer result = entityManager.merge(transfer);

        entityManager.getTransaction().commit();
        return result;
    }

    @Override
    public Optional<Transfer> getTransferById(Long transferId) {

        if (transferId == null) {
            return Optional.empty();
        }

        final EntityManager entityManager = entityManagerFactory.createEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transfer> criteriaQuery = criteriaBuilder.createQuery(Transfer.class);

        Root<Transfer> root = criteriaQuery.from(Transfer.class);
        criteriaQuery.select(root)
                .where(criteriaBuilder.equal(root.get("transferId"), transferId));
        TypedQuery<Transfer> typedQuery = entityManager.createQuery(criteriaQuery);

        Optional<Transfer> result = typedQuery
                .setMaxResults(1)
                .getResultList()
                .stream()
                .findFirst();
        return result;
    }

    @Override
    public List<Transfer> getTransfersByAccountId(Long accountId) {

        if (accountId == null) {
            return Collections.emptyList();
        }

        final EntityManager entityManager = entityManagerFactory.createEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transfer> criteriaQuery = criteriaBuilder.createQuery(Transfer.class);

        Root<Transfer> root = criteriaQuery.from(Transfer.class);
        criteriaQuery.select(root)
                .where(criteriaBuilder.or(
                        criteriaBuilder.equal(root.get("senderId"), accountId),
                        criteriaBuilder.equal(root.get("recipientId"), accountId))
                );
        TypedQuery<Transfer> typedQuery = entityManager.createQuery(criteriaQuery);

        List<Transfer> result = typedQuery.getResultList();
        return result;
    }
}
