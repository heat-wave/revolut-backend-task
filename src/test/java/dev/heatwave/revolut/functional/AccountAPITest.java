package dev.heatwave.revolut.functional;

import dev.heatwave.revolut.Application;
import dev.heatwave.revolut.model.Account;
import dev.heatwave.revolut.model.Currency;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;
import static org.hamcrest.Matchers.*;

class AccountAPITest {

    @BeforeEach
    void setUp() {
        new Application();
        Application.main(new String[]{});
    }

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
}
