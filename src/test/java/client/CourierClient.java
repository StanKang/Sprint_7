package client;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.Courier;
import models.CourierCredentials;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

public class CourierClient {

    private RequestSpecification baseSpec() {
        return given()
                .filter(new AllureRestAssured())
                .baseUri(Endpoints.BASE_URL)
                .contentType(JSON);
    }

    @Step("Создание курьера")
    public Response create(Courier courier) {
        return baseSpec()
                .body(courier)
                .post("/api/v1/courier");
    }

    @Step("Авторизация курьера")
    public Response login(CourierCredentials credentials) {
        return baseSpec()
                .body(credentials)
                .post("/api/v1/courier/login");
    }

    @Step("Удаление курьера с id={id}")
    public Response delete(int id) {
        return baseSpec()
                .delete("/api/v1/courier/" + id);
    }
}
