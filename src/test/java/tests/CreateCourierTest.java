package tests;

import client.CourierClient;
import models.Courier;
import models.CourierCredentials;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CreateCourierTest {

    private final CourierClient courierClient = new CourierClient();
    private int courierId = -1;

    private Courier randomCourier() {
        String suffix = String.valueOf(System.currentTimeMillis());
        return new Courier("ninja" + suffix, "pass" + suffix, "Ivan");
    }

    private int loginAndGetId(Courier courier) {
        return courierClient.login(new CourierCredentials(courier.getLogin(), courier.getPassword()))
                .jsonPath().getInt("id");
    }

    @AfterEach
    void tearDown() {
        if (courierId > 0) {
            courierClient.delete(courierId);
            courierId = -1;
        }
    }

    @Test
    @DisplayName("Успешное создание курьера")
    void createCourierSuccess() {
        Courier courier = randomCourier();
        courierClient.create(courier)
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));
        courierId = loginAndGetId(courier);
    }

    @Test
    @DisplayName("Нельзя создать дубликат курьера")
    void cannotCreateDuplicateCourier() {
        Courier courier = randomCourier();
        courierClient.create(courier);
        courierId = loginAndGetId(courier);

        courierClient.create(courier)
                .then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @DisplayName("Создание курьера без логина возвращает 400")
    void createCourierWithoutLoginReturns400() {
        courierClient.create(new Courier(null, "password123", "Ivan"))
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Создание курьера без пароля возвращает 400")
    void createCourierWithoutPasswordReturns400() {
        courierClient.create(new Courier("ninja" + System.currentTimeMillis(), null, "Ivan"))
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Создание курьера с существующим логином возвращает 409")
    void createCourierWithExistingLoginReturns409() {
        Courier courier = randomCourier();
        courierClient.create(courier);
        courierId = loginAndGetId(courier);

        courierClient.create(new Courier(courier.getLogin(), "otherpass", "Other"))
                .then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }
}
