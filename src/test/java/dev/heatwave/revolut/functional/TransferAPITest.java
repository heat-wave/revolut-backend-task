package dev.heatwave.revolut.functional;

import dev.heatwave.revolut.model.Currency;
import dev.heatwave.revolut.model.Transfer;
import io.restassured.RestAssured;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_INTEGER;
import static org.hamcrest.Matchers.*;

class TransferAPITest extends APITest {

    @Test
    void when__valid_transfer_posted__then__creates_successfully() {
        final BigDecimal senderStartBalance = BigDecimal.valueOf(100L);
        final BigDecimal recipientStartBalance = BigDecimal.valueOf(30L);
        final Long accountId1 = createAccount("user1", senderStartBalance, Currency.GBP);
        final Long accountId2 = createAccount("user2", recipientStartBalance, Currency.GBP);

        final BigDecimal amount = BigDecimal.valueOf(17);
        final Transfer payload = new Transfer(accountId1, accountId2, amount);

        given()
                .config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)
                        .numberReturnType(BIG_INTEGER)))
                .with().body(payload)
                .log().all()
        .when()
                .post("/api/transfer")
        .then()
                .statusCode(HttpStatus.OK_200)
                .and().body("transferId", notNullValue(Long.class))
                .and().body("senderId", equalTo(BigInteger.valueOf(accountId1)))
                .and().body("recipientId", equalTo(BigInteger.valueOf(accountId2)))
                .and().body("amount", comparesEqualTo(amount));

        given()
                .config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)
                        .numberReturnType(BIG_INTEGER)))
                .with()
                .log().all()
        .when()
                .get("/api/account/{id}", accountId1)
        .then()
                .statusCode(HttpStatus.OK_200)
                .and().body("balance", comparesEqualTo(senderStartBalance.subtract(amount)));

        given()
                .config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)
                        .numberReturnType(BIG_INTEGER)))
                .with()
                .log().all()
        .when()
                .get("/api/account/{id}", accountId2)
        .then()
                .statusCode(HttpStatus.OK_200)
                .and().body("balance", comparesEqualTo(recipientStartBalance.add(amount)));
    }

    @Test
    void when__nonexistent_sender_id__then__returns_not_found() {
        final Long accountId1 = createAccount("user1", BigDecimal.TEN, Currency.GBP);
        final Long accountId2 = 10000L;

        final BigDecimal amount = BigDecimal.ONE;
        final Transfer payload = new Transfer(accountId1, accountId2, amount);

        given()
                .config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)
                        .numberReturnType(BIG_INTEGER)))
                .with().body(payload)
                .log().all()
        .when()
                .post("/api/transfer")
        .then()
                .statusCode(HttpStatus.NOT_FOUND_404);
    }

    @Test
    void when__nonexistent_recipient_id__then__returns_not_found() {
        final Long accountId1 = 10000L;
        final Long accountId2 = createAccount("user2", BigDecimal.TEN, Currency.GBP);

        final BigDecimal amount = BigDecimal.ONE;
        final Transfer payload = new Transfer(accountId1, accountId2, amount);

        given()
                .config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)
                        .numberReturnType(BIG_INTEGER)))
                .with().body(payload)
                .log().all()
        .when()
                .post("/api/transfer")
        .then()
                .statusCode(HttpStatus.NOT_FOUND_404);
    }

    @Test
    void when__missing_sender_id__then__returns_not_found() {
        final Long accountId1 = createAccount("user1", BigDecimal.TEN, Currency.GBP);
        final Long accountId2 = null;

        final BigDecimal amount = BigDecimal.ONE;
        final Transfer payload = new Transfer(accountId1, accountId2, amount);

        given()
                .config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)
                        .numberReturnType(BIG_INTEGER)))
                .with().body(payload)
                .log().all()
        .when()
                .post("/api/transfer")
        .then()
                .statusCode(HttpStatus.NOT_FOUND_404);
    }

    @Test
    void when__missing_recipient_id__then__returns_not_found() {
        final Long accountId1 = null;
        final Long accountId2 = createAccount("user2", BigDecimal.TEN, Currency.GBP);

        final BigDecimal amount = BigDecimal.ONE;
        final Transfer payload = new Transfer(accountId1, accountId2, amount);

        given()
                .config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)
                        .numberReturnType(BIG_INTEGER)))
                .with().body(payload)
                .log().all()
        .when()
                .post("/api/transfer")
        .then()
                .statusCode(HttpStatus.NOT_FOUND_404);
    }

    @Test
    void when__missing_amount__then__returns_bad_request() {
        final Long accountId1 = createAccount("user1", BigDecimal.TEN, Currency.GBP);
        final Long accountId2 = createAccount("user2", BigDecimal.TEN, Currency.GBP);

        final Transfer payload = new Transfer(accountId1, accountId2, null);

        given()
                .config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)
                        .numberReturnType(BIG_INTEGER)))
                .with().body(payload)
                .log().all()
        .when()
                .post("/api/transfer")
        .then()
                .statusCode(HttpStatus.BAD_REQUEST_400);
    }

    @Test
    void when__negative_amount__then__returns_bad_request() {
        final Long accountId1 = createAccount("user1", BigDecimal.TEN, Currency.GBP);
        final Long accountId2 = createAccount("user2", BigDecimal.TEN, Currency.GBP);

        final BigDecimal amount = BigDecimal.ONE.negate();
        final Transfer payload = new Transfer(accountId1, accountId2, amount);

        given()
                .config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)
                        .numberReturnType(BIG_INTEGER)))
                .with().body(payload)
                .log().all()
        .when()
                .post("/api/transfer")
        .then()
                .statusCode(HttpStatus.BAD_REQUEST_400);
    }

    @Test
    void when__zero_amount__then__returns_bad_request() {
        final Long accountId1 = createAccount("user1", BigDecimal.TEN, Currency.GBP);
        final Long accountId2 = createAccount("user2", BigDecimal.TEN, Currency.GBP);

        final BigDecimal amount = BigDecimal.ZERO;
        final Transfer payload = new Transfer(accountId1, accountId2, amount);

        given()
                .config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)
                        .numberReturnType(BIG_INTEGER)))
                .with().body(payload)
                .log().all()
        .when()
                .post("/api/transfer")
        .then()
                .statusCode(HttpStatus.BAD_REQUEST_400);
    }

    @Test
    void when_currencies_mismatch__then__returns_bad_request() {
        final Long accountId1 = createAccount("user1", BigDecimal.TEN, Currency.GBP);
        final Long accountId2 = createAccount("user2", BigDecimal.TEN, Currency.USD);

        final BigDecimal amount = BigDecimal.ONE;
        final Transfer payload = new Transfer(accountId1, accountId2, amount);

        given()
                .config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)
                        .numberReturnType(BIG_INTEGER)))
                .with().body(payload)
                .log().all()
        .when()
                .post("/api/transfer")
        .then()
                .statusCode(HttpStatus.BAD_REQUEST_400);
    }

    @Test
    void when_insufficient_sender_funds__then__returns_bad_request() {
        final Long accountId1 = createAccount("user1", BigDecimal.ONE, Currency.GBP);
        final Long accountId2 = createAccount("user2", BigDecimal.TEN, Currency.USD);

        final BigDecimal amount = BigDecimal.valueOf(2L);
        final Transfer payload = new Transfer(accountId1, accountId2, amount);

        given()
                .config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)
                        .numberReturnType(BIG_INTEGER)))
                .with().body(payload)
                .log().all()
        .when()
                .post("/api/transfer")
        .then()
                .statusCode(HttpStatus.BAD_REQUEST_400);
    }

    @Test
    void when__valid_id_provided__then__finds_transfer() {
        final Long accountId1 = createAccount("user1", BigDecimal.TEN, Currency.GBP);
        final Long accountId2 = createAccount("user2", BigDecimal.TEN, Currency.GBP);

        final BigDecimal amount = BigDecimal.ONE;
        final Long transferId = createTransfer(accountId1, accountId2, amount);

        given()
                .config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)
                        .numberReturnType(BIG_INTEGER)))
                .with()
                .log().all()
        .when()
                .get("/api/transfer/{id}", transferId)
        .then()
                .statusCode(HttpStatus.OK_200)
                .and().body("transferId", equalTo(BigInteger.valueOf(transferId)))
                .and().body("senderId", equalTo(BigInteger.valueOf(accountId1)))
                .and().body("recipientId", equalTo(BigInteger.valueOf(accountId2)))
                .and().body("amount", comparesEqualTo(amount));
    }

    @Test
    void when__nonexistent_id_provided__then__returns_not_found() {
        final int nonExistentId = 10000;

        given()
                .with()
                .log().all()
        .when()
                .get("/api/transfer/{id}", nonExistentId)
        .then()
                .statusCode(HttpStatus.NOT_FOUND_404);
    }
}
