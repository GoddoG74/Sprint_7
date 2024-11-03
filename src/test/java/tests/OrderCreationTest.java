package tests;

import helpers.CourierClient;
import helpers.Order;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderCreationTest {

    private final List<String> color;

    public OrderCreationTest(List<String> color) {
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
                {Arrays.asList("BLACK")},       // Тест с цветом BLACK
                {Arrays.asList("GREY")},        // Тест с цветом GREY
                {Arrays.asList("BLACK", "GREY")}, // Тест с цветами BLACK и GREY
                {Arrays.asList()}               // Тест без указания цвета
        });
    }

    @Test
    @Description("Тест на создание заказа с различными цветами")
    public void testCreateOrderWithVariousColors() {
        Order order = createOrderWithColors(color);
        Response response = sendCreateOrderRequest(order);
        validateOrderCreation(response);
    }

    @Step("Создание заказа с цветами: {color}")
    private Order createOrderWithColors(List<String> color) {
        return new Order(color);
    }

    @Step("Отправка запроса на создание заказа")
    private Response sendCreateOrderRequest(Order order) {
        return given()
                .header("Content-Type", "application/json")
                .body(order)
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
