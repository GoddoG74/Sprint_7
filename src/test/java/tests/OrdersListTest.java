package tests;

import helpers.CourierClient;
import io.qameta.allure.Description;
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
    @Description("Тест на получение списка заказов и проверка структуры ответа")
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

    @Step("Проверка структуры и содержания ответа на запрос списка заказов")
    private void validateOrdersListResponse(Response response) {
        response.then().log().body(); // Логирование тела ответа для удобства проверки
        response.then()
                .body("orders", not(empty())) // Проверка, что список заказов не пуст
                .body("orders.size()", greaterThan(0)) // Содержит хотя бы один заказ
                .body("orders[0].id", notNullValue()) // Проверка, что у заказа есть ID
                .body("orders[0].firstName", notNullValue()) // Имя клиента
                .body("orders[0].lastName", notNullValue()) // Фамилия клиента
                .body("orders[0].address", notNullValue()) // Адрес
                .body("orders[0].metroStation", notNullValue()) // Станция метро
                .body("orders[0].phone", notNullValue()) // Телефон
                .body("orders[0].rentTime", greaterThan(0)) // Время аренды больше 0
                .body("orders[0].deliveryDate", notNullValue()) // Дата доставки
                .body("orders[0].track", notNullValue()); // Номер отслеживания
    }
}
