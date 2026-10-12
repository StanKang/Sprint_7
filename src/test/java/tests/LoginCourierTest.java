package tests;

import client.CourierClient;
import models.Courier;
import models.CourierCredentials;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

public class LoginCourierTest {

    private final CourierClient courierClient = new CourierClient();
    private Courier courier;
    private int courierId = -1;

    @BeforeEach
    void setUp() {
        String suffix = String.valueOf(System.currentTimeMillis());
        courier = new Courier("ninja" + suffix, "pass" + suffix, "Ivan");
        courierClient.create(courier);
    }

    @AfterEach
    void tearDown() {
        if (courierId > 0) {
            courierClient.delete(courierId);
        } else {
            try {
                int id = courierClient.login(new CourierCredentials(courier.getLogin(), courier.getPassword()))
                        .jsonPath().getInt("id");
                if (id > 0) courierClient.delete(id);
            } catch (Exception ignored) {}
        }
    }

    @Test
    @DisplayName("Успешная авторизация курьера")
    void loginSuccess() {
        courierId = courierClient.login(new CourierCredentials(courier.getLogin(), courier.getPassword()))
                .then()
                .statusCode(200)
                .body("id", greaterThan(0))
                .extract().jsonPath().getInt("id");
    }

    @Test
    @DisplayName("Авторизация без логина возвращает 400")
    void loginWithoutLoginReturns400() {
        courierClient.login(new CourierCredentials(null, courier.getPassword()))
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Авторизация без пароля возвращает 400")
    void loginWithoutPasswordReturns400() {
        courierClient.login(new CourierCredentials(courier.getLogin(), ""))
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Авторизация с неверным логином возвращает 404")
    void loginWithWrongLoginReturns404() {
        courierClient.login(new CourierCredentials("wronglogin_xyz", courier.getPassword()))
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Авторизация с неверным паролем возвращает 404")
    void loginWithWrongPasswordReturns404() {
        courierClient.login(new CourierCredentials(courier.getLogin(), "wrongpassword_xyz"))
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }
}
