package tests;

import client.OrderClient;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.empty;

public class GetOrderListTest {

    private final OrderClient orderClient = new OrderClient();

    @Test
    void getOrderListReturnsNonEmptyList() {
        orderClient.getOrderList()
                .then()
                .statusCode(200)
                .body("orders", not(empty()));
    }
}
