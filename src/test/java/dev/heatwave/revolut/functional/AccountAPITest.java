package dev.heatwave.revolut.functional;

import dev.heatwave.revolut.model.Account;
import dev.heatwave.revolut.model.Currency;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_INTEGER;
import static org.hamcrest.Matchers.*;

class AccountAPITest extends APITest {

    @Test
    void when__valid_new_account_posted__then__creates_successfully() {
        final String accountHolderName = "user1";
        final BigDecimal balance = BigDecimal.TEN;
        final Currency currency = Currency.GBP;
        final Account payload = Account.AccountBuilder.builder()
                .accountHolderName(accountHolderName)
                .balance(balance)
                .currency(currency)
                .build();
        given()
                .config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .with().body(payload)
                .log().all()
        .when()
                .post("/api/account")
        .then()
                .statusCode(HttpStatus.CREATED_201)
                .and().body("accountId", notNullValue(Long.class))
                .and().body("accountHolderName", equalTo(accountHolderName))
                .and().body("currency", equalTo(currency.toString()))
                .and().body("balance", comparesEqualTo(balance));
    }

    @Test
    void when__new_account_missing_balance__then__fails() {
        final String accountHolderName = "user1";
        final Currency currency = Currency.GBP;
        final Account payload = Account.AccountBuilder.builder()
                .accountHolderName(accountHolderName)
                .currency(currency)
                .build();
        given()
                .with().body(payload).contentType(ContentType.JSON)
                .log().all()
        .when()
                .post("/api/account")
        .then()
                .statusCode(HttpStatus.BAD_REQUEST_400);
    }

    @Test
    void when__new_account_missing_currency__then__fails() {
        final String accountHolderName = "user1";
        final BigDecimal balance = BigDecimal.TEN;
        final Account payload = Account.AccountBuilder.builder()
                .accountHolderName(accountHolderName)
                .balance(balance)
                .build();
        given()
                .with().body(payload).contentType(ContentType.JSON)
                .log().all()
        .when()
                .post("/api/account")
        .then()
                .statusCode(HttpStatus.BAD_REQUEST_400);
    }

    @Test
    void when__new_account_is_null__then__fails() {
        given()
                .with().contentType(ContentType.JSON)
                .log().all()
        .when()
                .post("/api/account")
        .then()
                .statusCode(HttpStatus.BAD_REQUEST_400);
    }

    @Test
    void when__valid_id_provided__then__finds_account() {
        final String accountHolderName = "user1";
        final BigDecimal balance = BigDecimal.TEN;
        final Currency currency = Currency.GBP;

        final Long id = createAccount(accountHolderName, balance, currency);

        given()
                .config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)
                        .numberReturnType(BIG_INTEGER)))
                .with()
                .log().all()
        .when()
                .get("/api/account/{id}", id)
        .then()
                .statusCode(HttpStatus.OK_200)
                .and().body("accountId", equalTo(BigInteger.valueOf(id)))
                .and().body("accountHolderName", equalTo(accountHolderName))
                .and().body("currency", equalTo(currency.toString()))
                .and().body("balance", comparesEqualTo(balance));
    }

    @Test
    void when__nonexistent_id_provided__then__returns_not_found() {
        final int nonExistentId = 10000;

        given()
                .with()
                .log().all()
        .when()
                .get("/api/account/{id}", nonExistentId)
        .then()
                .statusCode(HttpStatus.NOT_FOUND_404);
    }

    @Test
    void when__listing_transfers__then_only_users_transfers_shown() {
        final Long accountId1 = createAccount("user1", BigDecimal.TEN, Currency.GBP);
        final Long accountId2 = createAccount("user2", BigDecimal.TEN, Currency.GBP);
        final Long accountId3 = createAccount("user3", BigDecimal.TEN, Currency.GBP);
        createTransfer(accountId1, accountId2, BigDecimal.ONE);
        createTransfer(accountId1, accountId3, BigDecimal.ONE);
        createTransfer(accountId3, accountId1, BigDecimal.ONE);

        given()
                .with()
                .log().all()
        .when()
                .get("/api/account/{id}/transfers", accountId3)
        .then()
                .statusCode(HttpStatus.OK_200)
                .and().body("size()", is(2));
    }
}
