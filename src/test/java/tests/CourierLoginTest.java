package tests;

import helpers.CourierClient;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CourierLoginTest {

    private CourierClient courierClient;
    private int courierId;
    private String uniqueLogin;
    private String password;
    private String firstName;

    @Before
    @Step("Инициализация данных перед тестом")
    public void setUp() {
        courierClient = new CourierClient();
        uniqueLogin = CourierClient.generateUniqueLogin();
        password = CourierClient.generatePassword();
        firstName = CourierClient.generateFirstName();
        createTestCourier(uniqueLogin, password, firstName);
    }

    @After
    @Step("Очистка данных после теста")
    public void tearDown() {
        if (courierId != 0) {
            deleteTestCourier(courierId);
        }
    }

    @Step("Создание курьера для теста")
    private void createTestCourier(String login, String password, String firstName) {
        courierClient.createCourier(login, password, firstName);
    }

    @Step("Удаление курьера после теста")
    private void deleteTestCourier(int courierId) {
        courierClient.deleteCourier(courierId);
    }

    @Test
    @Description("Проверка успешной авторизации курьера")
    @Step("Тест успешной авторизации курьера")
    public void testCourierCanLoginSuccessfully() {
        Response response = courierClient.loginCourier(uniqueLogin, password);
        response.then()
                .statusCode(200)
                .body("id", notNullValue());
        courierId = response.path("id");
    }

    @Test
    @Description("Проверка ошибки авторизации при отсутствии логина в запросе")
    @Step("Тест авторизации с отсутствующим логином в теле запроса")
    public void testCourierCannotLoginWhenLoginIsMissing() {
        Response response = courierClient.loginCourier(null, password);
        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @Description("Проверка ошибки авторизации при отсутствии пароля в запросе")
    @Step("Тест авторизации с отсутствующим паролем в теле запроса")
    public void testCourierCannotLoginWhenPasswordIsMissing() {
        Response response = courierClient.loginCourier(uniqueLogin, null);
        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @Description("Проверка ошибки авторизации при неверном логине")
    @Step("Тест авторизации с неверным логином")
    public void testCourierCannotLoginWithIncorrectLogin() {
        Response response = courierClient.loginCourier("incorrectLogin", password);
        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @Description("Проверка ошибки авторизации при неверном пароле")
    @Step("Тест авторизации с неверным паролем")
    public void testCourierCannotLoginWithIncorrectPassword() {
        Response response = courierClient.loginCourier(uniqueLogin, "incorrectPassword");
        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @Description("Проверка ошибки авторизации для несуществующего пользователя")
    @Step("Тест авторизации с несуществующим пользователем")
    public void testCourierCannotLoginWithNonExistentUser() {
        String nonExistentLogin = CourierClient.generateUniqueLogin();
        Response response = courierClient.loginCourier(nonExistentLogin, CourierClient.generatePassword());
        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }
}
