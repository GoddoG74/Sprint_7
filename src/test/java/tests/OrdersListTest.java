package tests;

import helpers.CourierClient;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class OrdersListTest {

    @BeforeClass
    @Step("Установка базового URL через CourierClient")
    public static void setUpBaseUrl() {
        new CourierClient();
    }

    @Test
    @Step("Тест на получение списка заказов")
    public void testGetOrdersList() {
        Response response = sendGetOrdersRequest();
        validateOrdersListResponse(response);
    }

    @Step("Отправка запроса на получение списка заказов")
    private Response sendGetOrdersRequest() {
        return given()
                .header("Content-Type", "application/json")
                .log().all() // Логирование запроса
                .when()
                .get("/api/v1/orders")
                .then()
                .log().all() // Логирование ответа
                .statusCode(200)
                .extract().response();
    }

    @Step("Проверка, что поле orders не пустое")
    private void validateOrdersListResponse(Response response) {
        response.then()
                .body("orders", not(empty()));
    }
}
