package tests;

import helpers.CourierClient;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CourierLoginTest {

    private CourierClient courierClient;
    private int courierId;
    private String uniqueLogin;
    private String password;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        uniqueLogin = generateUniqueLogin();
        password = "1234";
        createTestCourier(uniqueLogin, password);
    }

    @After
    public void tearDown() {
        if (courierId != 0) {
            deleteTestCourier(courierId);
        }
    }

    @Step("Генерация уникального логина")
    private String generateUniqueLogin() {
        return "Courier" + System.currentTimeMillis();
    }

    @Step("Создание курьера для теста")
    private void createTestCourier(String login, String password) {
        courierClient.createCourier(login, password, "testName");
    }

    @Step("Удаление курьера после теста")
    private void deleteTestCourier(int courierId) {
        courierClient.deleteCourier(courierId);
    }

    @Test
    @Step("Тест успешной авторизации курьера")
    public void testCourierCanLoginSuccessfully() {
        Response response = courierClient.loginCourier(uniqueLogin, password);
        response.then()
                .statusCode(200)
                .body("id", notNullValue());
        courierId = response.path("id");
    }

    @Test
    @Step("Тест авторизации с отсутствующим логином в теле запроса")
    public void testCourierCannotLoginWhenLoginIsMissing() {
        // Тело запроса содержит только password, без login
        String requestBody = String.format("{\"password\": \"%s\"}", password);
        Response response = given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .log().all()
                .when()
                .post("/api/v1/courier/login");

        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @Step("Тест авторизации с отсутствующим паролем в теле запроса")
    public void testCourierCannotLoginWhenPasswordIsMissing() {
        // Тело запроса содержит только login, без password
        String requestBody = String.format("{\"login\": \"%s\"}", uniqueLogin);
        Response response = given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .log().all()
                .when()
                .post("/api/v1/courier/login");

        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @Step("Тест авторизации без логина")
    public void testCourierCannotLoginWithoutLogin() {
        Response response = courierClient.loginCourier("", password);
        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @Step("Тест авторизации без пароля")
    public void testCourierCannotLoginWithoutPassword() {
        Response response = courierClient.loginCourier(uniqueLogin, "");
        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @Step("Тест авторизации с неверным логином")
    public void testCourierCannotLoginWithIncorrectLogin() {
        Response response = courierClient.loginCourier("incorrectLogin", password);
        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @Step("Тест авторизации с неверным паролем")
    public void testCourierCannotLoginWithIncorrectPassword() {
        Response response = courierClient.loginCourier(uniqueLogin, "incorrectPassword");
        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @Step("Тест авторизации с несуществующим пользователем")
    public void testCourierCannotLoginWithNonExistentUser() {
        String nonExistentLogin = generateUniqueLogin();
        Response response = courierClient.loginCourier(nonExistentLogin, "randomPassword");
        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }
}
