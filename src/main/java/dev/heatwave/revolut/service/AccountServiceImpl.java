package dev.heatwave.revolut.service;

import dev.heatwave.revolut.model.account.Account;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Optional;

public class AccountServiceImpl implements AccountService {
    private EntityManagerFactory entityManagerFactory;

    public AccountServiceImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Account createAccount(Account account) {
        final EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        try {
            Account result = entityManager.merge(account);
            entityManager.getTransaction().commit();
            return result;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Optional<Account> getAccountById(long accountId) {
        final EntityManager entityManager = entityManagerFactory.createEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Account> criteriaQuery = criteriaBuilder.createQuery(Account.class);

        Root<Account> root = criteriaQuery.from(Account.class);
        criteriaQuery.select(root)
                .where(criteriaBuilder.equal(root.get("accountId"), accountId));
        TypedQuery<Account> typedQuery = entityManager.createQuery(criteriaQuery);

        Optional<Account> result = typedQuery
                .setMaxResults(1)
                .getResultList()
                .stream()
                .findFirst();
        return result;
    }
}
