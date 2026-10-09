package tests;

import client.OrderClient;
import models.Order;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.notNullValue;

public class CreateOrderTest {

    private final OrderClient orderClient = new OrderClient();

    static Stream<List<String>> colorProvider() {
        return Stream.of(
                Arrays.asList("BLACK"),
                Arrays.asList("GREY"),
                Arrays.asList("BLACK", "GREY"),
                null
        );
    }

    @ParameterizedTest
    @MethodSource("colorProvider")
    void createOrderWithColor(List<String> color) {
        Order order = new Order(
                "Иван", "Иванов", "Москва, ул. Ленина 1", "Сокольники",
                "+79991234567", 3, "2024-12-31", "Позвоните заранее", color
        );

        orderClient.createOrder(order)
                .then()
                .statusCode(201)
                .body("track", notNullValue());
    }
}
