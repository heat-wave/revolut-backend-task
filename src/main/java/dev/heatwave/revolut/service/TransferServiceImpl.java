package dev.heatwave.revolut.service;

import dev.heatwave.revolut.model.account.Account;
import dev.heatwave.revolut.model.transfer.Transfer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

public class TransferServiceImpl implements TransferService {
    private AccountService accountService;
    private EntityManagerFactory entityManagerFactory;

    public TransferServiceImpl(AccountService accountService, EntityManagerFactory entityManagerFactory) {
        this.accountService = accountService;
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Transfer createTransfer(Transfer transfer) {
        final EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        final Optional<Account> sender = accountService.getAccountById(transfer.getSenderId());
        final Optional<Account> recipient = accountService.getAccountById(transfer.getRecipientId());
        if (!sender.isPresent()) {
            throw new RuntimeException();
            //bad request: wrong sender id
        }
        if (!recipient.isPresent()) {
            throw new RuntimeException();
            //bad request: wrong recipient id
        }
        //bad request if currencies mismatch
        if (sender.get().getBalance().compareTo(transfer.getAmount()) < 0) {
            throw new RuntimeException();
            //bad request: insufficient funds
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
    public Optional<Transfer> getTransferById(long transferId) {
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
    public List<Transfer> getTransfersByAccountId(long accountId) {
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
