package dev.heatwave.revolut.service;

import com.google.inject.Singleton;
import dev.heatwave.revolut.exception.ForbiddenOperationException;
import dev.heatwave.revolut.model.Account;
import dev.heatwave.revolut.model.Currency;
import dev.heatwave.revolut.persistence.PersistenceManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
public class AccountServiceImpl implements AccountService {

    private final EntityManagerFactory entityManagerFactory = PersistenceManager.getEntityManagerFactory();

    AccountServiceImpl() {}

    @Override
    public Account createAccount(Account account) {

        if (account == null) {
            throw new ForbiddenOperationException("Account must not be null");
        }
        if (account.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new ForbiddenOperationException("Account starting balance must not be negative");
        }
        if (account.getCurrency() == null) {
            throw new ForbiddenOperationException("No valid account currency specified. Supported currencies are: %s.",
                    Arrays.stream(Currency.values())
                            .map(Enum::toString)
                            .collect(Collectors.joining(",")));
        }
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
    public Optional<Account> getAccountById(Long accountId) {

        if (accountId == null) {
            return Optional.empty();
        }

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
