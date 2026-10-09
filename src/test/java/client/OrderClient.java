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

    @Step("Принять заказ id={orderId} курьером id={courierId}")
    public Response acceptOrder(int orderId, int courierId) {
        return baseSpec()
                .queryParam("courierId", courierId)
                .put("/api/v1/orders/accept/" + orderId);
    }

    @Step("Принять заказ id={orderId} без courierId")
    public Response acceptOrderWithoutCourier(int orderId) {
        return baseSpec()
                .put("/api/v1/orders/accept/" + orderId);
    }

    @Step("Получить заказ по треку {track}")
    public Response getOrderByTrack(int track) {
        return baseSpec()
                .queryParam("t", track)
                .get("/api/v1/orders/track");
    }

    @Step("Получить заказ без трека")
    public Response getOrderWithoutTrack() {
        return baseSpec()
                .get("/api/v1/orders/track");
    }
}
