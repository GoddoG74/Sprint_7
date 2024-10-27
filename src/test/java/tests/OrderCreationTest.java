package tests;

import helpers.CourierClient;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderCreationTest {

    private final String[] color;

    public OrderCreationTest(String[] color) {
        this.color = color;
    }

    @BeforeClass
    @Step("Установка базового URL через CourierClient")
    public static void setUpBaseUrl() {
        new CourierClient();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> orderData() {
        return Arrays.asList(new Object[][]{
                {new String[]{"BLACK"}},       // Тест с цветом BLACK
                {new String[]{"GREY"}},        // Тест с цветом GREY
                {new String[]{"BLACK", "GREY"}}, // Тест с цветами BLACK и GREY
                {new String[]{}}               // Тест без указания цвета
        });
    }

    @Test
    @Step("Тест на создание заказа с различными цветами: {color}")
    public void testCreateOrderWithVariousColors() {
        String colorJson = formatColorJson(color);
        String requestBody = createOrderRequestBody(colorJson);
        Response response = sendCreateOrderRequest(requestBody);
        validateOrderCreation(response);
    }

    @Step("Форматирование цвета в JSON: {color}")
    private String formatColorJson(String[] color) {
        return Arrays.stream(color)
                .map(c -> "\"" + c + "\"")
                .collect(Collectors.joining(", "));
    }

    @Step("Создание тела запроса для заказа")
    private String createOrderRequestBody(String colorJson) {
        return String.format("{ \"firstName\": \"Naruto\", \"lastName\": \"Uchiha\", \"address\": \"Konoha, 142 apt.\", " +
                "\"metroStation\": 4, \"phone\": \"+7 800 355 35 35\", \"rentTime\": 5, \"deliveryDate\": \"2020-06-06\", " +
                "\"comment\": \"Saske, come back to Konoha\", \"color\": [%s] }", colorJson);
    }

    @Step("Отправка запроса на создание заказа")
    private Response sendCreateOrderRequest(String requestBody) {
        return given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .log().all() // Логирование запроса
                .when()
                .post("/api/v1/orders")
                .then()
                .log().all() // Логирование ответа
                .statusCode(201)
                .extract().response();
    }

    @Step("Проверка, что заказ был создан, и поле track присутствует")
    private void validateOrderCreation(Response response) {
        response.then()
                .body("track", notNullValue());
    }
}
