package tests;

import client.CourierClient;
import client.OrderClient;
import models.Courier;
import models.CourierCredentials;
import models.Order;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;

public class AcceptOrderTest {

    private final CourierClient courierClient = new CourierClient();
    private final OrderClient orderClient = new OrderClient();
    private int courierId = -1;
    private int orderId = -1;

    @BeforeEach
    void setUp() {
        String suffix = String.valueOf(System.currentTimeMillis());
        Courier courier = new Courier("ninja" + suffix, "pass" + suffix, "Ivan");
        courierClient.create(courier);
        courierId = courierClient.login(new CourierCredentials(courier.getLogin(), courier.getPassword()))
                .jsonPath().getInt("id");

        Order order = new Order("Иван", "Иванов", "Москва, ул. Ленина 1", "Сокольники",
                "+79991234567", 3, "2024-12-31", "Тест", List.of("BLACK"));
        int track = orderClient.createOrder(order).jsonPath().getInt("track");
        orderId = orderClient.getOrderByTrack(track).jsonPath().getInt("order.id");
    }

    @AfterEach
    void tearDown() {
        if (courierId > 0) {
            courierClient.delete(courierId);
        }
    }

    @Test
    @DisplayName("Успешное принятие заказа")
    void acceptOrderSuccess() {
        orderClient.acceptOrder(orderId, courierId)
                .then()
                .statusCode(200)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Принятие заказа без id курьера возвращает 400")
    void acceptOrderWithoutCourierIdReturns400() {
        orderClient.acceptOrderWithoutCourier(orderId)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Принятие заказа с несуществующим id курьера возвращает 404")
    void acceptOrderWithWrongCourierIdReturns404() {
        orderClient.acceptOrder(orderId, 99999999)
                .then()
                .statusCode(404)
                .body("message", equalTo("Курьера с таким id не существует"));
    }

    @Test
    @DisplayName("Принятие заказа с несуществующим id заказа возвращает 404")
    void acceptOrderWithWrongOrderIdReturns404() {
        orderClient.acceptOrder(99999999, courierId)
                .then()
                .statusCode(404)
                .body("message", equalTo("Заказа с таким id не существует"));
    }
}
