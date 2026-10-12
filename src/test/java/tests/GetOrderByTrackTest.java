package tests;

import client.OrderClient;
import models.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class GetOrderByTrackTest {

    private final OrderClient orderClient = new OrderClient();

    private int createOrderAndGetTrack() {
        Order order = new Order("Иван", "Иванов", "Москва, ул. Ленина 1", "Сокольники",
                "+79991234567", 3, "2024-12-31", "Тест", List.of("BLACK"));
        return orderClient.createOrder(order).jsonPath().getInt("track");
    }

    @Test
    @DisplayName("Успешное получение заказа по номеру трека")
    void getOrderByTrackSuccess() {
        int track = createOrderAndGetTrack();

        orderClient.getOrderByTrack(track)
                .then()
                .statusCode(200)
                .body("order", notNullValue())
                .body("order.track", equalTo(track));
    }

    @Test
    @DisplayName("Запрос без номера трека возвращает 400")
    void getOrderWithoutTrackReturns400() {
        orderClient.getOrderWithoutTrack()
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Запрос с несуществующим номером трека возвращает 404")
    void getOrderWithNonExistentTrackReturns404() {
        orderClient.getOrderByTrack(99999999)
                .then()
                .statusCode(404)
                .body("message", equalTo("Заказ не найден"));
    }
}
