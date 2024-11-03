package helpers;

import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class CourierClient {

    private final String baseUrl = "/api/v1/courier";
    private final String loginUrl = "/api/v1/courier/login";
    private static final Faker faker = new Faker();

    public CourierClient() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Step("Получение базового URL")
    public static String getBaseUrl() {
        return RestAssured.baseURI;
    }

    @Step("Создание курьера с логином: {login}")
    public Response createCourier(String login, String password, String firstName) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("login", login);
        requestBody.put("password", password);
        requestBody.put("firstName", firstName);

        return given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .log().all()
                .when()
                .post(baseUrl)
                .then()
                .log().all()
                .extract().response();
    }

    @Step("Авторизация курьера с логином: {login}")
    public Response loginCourier(String login, String password) {
        Map<String, String> requestBody = new HashMap<>();
        if (login != null) {
            requestBody.put("login", login);
        }
        if (password != null) {
            requestBody.put("password", password);
        }

        return given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .log().all()
                .when()
                .post(loginUrl)
                .then()
                .log().all()
                .extract().response();
    }

    @Step("Авторизация курьера с логином: {login} и получением ID")
    public int loginAndGetCourierId(String login, String password) {
        Response response = loginCourier(login, password);
        response.then().statusCode(200);
        return response.path("id");
    }

    @Step("Удаление курьера с ID: {courierId}")
    public Response deleteCourier(int courierId) {
        String deleteUrl = baseUrl + "/" + courierId;

        return given()
                .header("Content-Type", "application/json")
                .log().all()
                .when()
                .delete(deleteUrl)
                .then()
                .log().all()
                .extract().response();
    }

    public static String generateUniqueLogin() {
        return "Courier" + System.currentTimeMillis();
    }

    public static String generatePassword() {
        return faker.internet().password();
    }

    public static String generateFirstName() {
        return faker.name().firstName();
    }
}
