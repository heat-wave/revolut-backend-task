package dev.heatwave.revolut;

import dev.heatwave.revolut.model.account.Account;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.math.BigDecimal;
import java.util.List;

import static spark.Spark.get;

public class Application {

    public static void main(String[] args) {

        new Application().run();

//        post("/account");
//        post("/account/:id/state");
//        get("/account/:id");
//        get("/account/:id/transactions");


    }

    public void run() {
        get("/hello", (request, response) -> {
                    try {
                        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("dev.heatwave.revolut");
                        EntityManager em = entityManagerFactory.createEntityManager();
                        Account account = new Account("user1", BigDecimal.ONE);
                        em.getTransaction().begin();
                        em.persist(account);
                        List<Account> accounts = em.createQuery("FROM Account").getResultList();
                        accounts.forEach(ac -> System.out.println(ac.toString()));
                        em.getTransaction().commit();
                    } finally {

                    }
                    return "";
                }
        );

        get("/hello1", (request, response) -> {
                    try {
                        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("dev.heatwave.revolut");
                        EntityManager em = entityManagerFactory.createEntityManager();
                        em.getTransaction().begin();
                        List<Account> accounts = em.createQuery("FROM Account").getResultList();
                        accounts.forEach(ac -> System.out.println(ac.toString()));
                        em.getTransaction().commit();
                    } finally {

                    }
                    return "";
                }
        );
    }
}
