package helpers;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class CourierClient {

    private final String baseUrl = "/api/v1/courier";
    private final String loginUrl = "/api/v1/courier/login";

    public CourierClient() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru"; // Установите точный URL API
    }

    // Метод для получения базового URL
    @Step("Получение базового URL")
    public static String getBaseUrl() {
        return RestAssured.baseURI;
    }

    // Метод для создания курьера
    @Step("Создание курьера с логином: {login}")
    public Response createCourier(String login, String password, String firstName) {
        String requestBody = String.format("{\"login\": \"%s\", \"password\": \"%s\", \"firstName\": \"%s\"}", login, password, firstName);

        return given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .log().all() // Логирование запроса
                .when()
                .post(baseUrl)
                .then()
                .log().all() // Логирование ответа
                .extract().response();
    }

    // Метод для авторизации курьера и получения id
    @Step("Авторизация курьера с логином: {login} и получением ID")
    public int loginAndGetCourierId(String login, String password) {
        Response loginResponse = loginCourier(login, password)
                .then()
                .statusCode(200) // Ожидаем успешную авторизацию
                .extract().response();
        return loginResponse.path("id");
    }

    // Метод для авторизации курьера и получения полного ответа
    @Step("Авторизация курьера с логином: {login}")
    public Response loginCourier(String login, String password) {
        String loginRequestBody = String.format("{\"login\": \"%s\", \"password\": \"%s\"}", login, password);

        return given()
                .header("Content-Type", "application/json")
                .body(loginRequestBody)
                .log().all() // Логирование запроса авторизации
                .when()
                .post(loginUrl)
                .then()
                .log().all() // Логирование ответа
                .extract().response();
    }

    // Метод для удаления курьера по ID
    @Step("Удаление курьера с ID: {courierId}")
    public Response deleteCourier(int courierId) {
        String deleteUrl = baseUrl + "/" + courierId;

        return given()
                .header("Content-Type", "application/json")
                .log().all() // Логирование запроса на удаление
                .when()
                .delete(deleteUrl)
                .then()
                .log().all() // Логирование ответа
                .extract().response();
    }
}
