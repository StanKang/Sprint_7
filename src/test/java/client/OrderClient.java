package client;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.Order;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

public class OrderClient {

    private RequestSpecification baseSpec() {
        return given()
                .filter(new AllureRestAssured())
                .baseUri(Endpoints.BASE_URL)
                .contentType(JSON);
    }

    @Step("Создание заказа")
    public Response createOrder(Order order) {
        return baseSpec()
                .body(order)
                .post("/api/v1/orders");
    }

    @Step("Получение списка заказов")
    public Response getOrderList() {
        return baseSpec()
                .get("/api/v1/orders");
    }
}
