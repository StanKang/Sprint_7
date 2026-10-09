package tests;

import client.CourierClient;
import models.Courier;
import models.CourierCredentials;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;

public class DeleteCourierTest {

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
    void deleteCourierSuccess() {
        Courier courier = randomCourier();
        courierClient.create(courier);
        courierId = loginAndGetId(courier);

        courierClient.delete(courierId)
                .then()
                .statusCode(200)
                .body("ok", equalTo(true));
        courierId = -1;
    }

    @Test
    void deleteCourierWithNonExistentIdReturns404() {
        courierClient.delete(99999999)
                .then()
                .statusCode(404)
                .body("message", equalTo("Курьера с таким id нет."));
    }
}
