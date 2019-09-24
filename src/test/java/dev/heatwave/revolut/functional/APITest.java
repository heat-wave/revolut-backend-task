package dev.heatwave.revolut.functional;

import com.google.inject.Guice;
import com.google.inject.Injector;
import dev.heatwave.revolut.configuration.GuiceConfiguration;
import dev.heatwave.revolut.model.Account;
import dev.heatwave.revolut.model.Currency;
import dev.heatwave.revolut.model.Transfer;
import dev.heatwave.revolut.persistence.PersistenceManager;
import dev.heatwave.revolut.rest.RestContext;
import dev.heatwave.revolut.rest.endpoint.AccountEndpoint;
import dev.heatwave.revolut.rest.endpoint.TransferEndpoint;
import dev.heatwave.revolut.util.PropertyResolver;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;

abstract class APITest {

    @AfterEach
    void dropDatabase() {
        PersistenceManager.clearEntityManagerFactory();
    }

    @BeforeAll
    static void setUp() {
        RestContext.init(PropertyResolver.getConfiguration().getInt(PropertyResolver.SERVER_PORT));
        Injector injector = Guice.createInjector(new GuiceConfiguration());

        RestContext.addEndpoint(injector.getInstance(AccountEndpoint.class));
        RestContext.addEndpoint(injector.getInstance(TransferEndpoint.class));
    }


    Long createAccount(String accountHolderName, BigDecimal balance, Currency currency) {
        final Account payload = Account.AccountBuilder.builder()
                .accountHolderName(accountHolderName)
                .balance(balance)
                .currency(currency)
                .build();

        return given()
                .config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .with().body(payload)
                .log().all()
                .when()
                .post("/api/account")
                .jsonPath().getLong("accountId");
    }

    Long createTransfer(Long senderId, Long recipientId, BigDecimal amount) {
        final Transfer payload = new Transfer(senderId, recipientId, amount);

        return given()
                .config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .with().body(payload)
                .log().all()
                .when()
                .post("/api/transfer")
                .jsonPath().getLong("transferId");
    }
}
