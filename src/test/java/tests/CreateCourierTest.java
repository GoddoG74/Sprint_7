package tests;

import helpers.CourierClient;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class CreateCourierTest {

    private CourierClient courierClient;
    private int courierId;
    private String uniqueLogin;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        uniqueLogin = generateUniqueLogin();
    }

    @After
    public void tearDown() {
        if (courierId != 0) {
            deleteCourier(courierId);
        }
    }

    @Step("Генерация уникального логина для курьера")
    private String generateUniqueLogin() {
        return "Courier" + System.currentTimeMillis();
    }

    @Step("Удаление курьера с ID: {courierId}")
    private void deleteCourier(int courierId) {
        Response deleteResponse = courierClient.deleteCourier(courierId);
        if (deleteResponse.getStatusCode() != 200) {
            System.err.println("Ошибка при удалении курьера. Статус: " + deleteResponse.getStatusCode());
        }
    }

    @Test
    @Step("Тест на успешное создание курьера")
    public void testCreateCourierSuccessfully() {
        Response response = courierClient.createCourier(uniqueLogin, "1234", "kurier");
        response.then()
                .statusCode(201)
                .body("ok", equalTo(true));

        courierId = courierClient.loginAndGetCourierId(uniqueLogin, "1234");
    }

    @Test
    @Step("Тест на предотвращение создания дублирующего курьера")
    public void testCannotCreateDuplicateCourier() {
        courierClient.createCourier(uniqueLogin, "1234", "kurier");

        Response duplicateResponse = courierClient.createCourier(uniqueLogin, "1234", "kurier");
        duplicateResponse.then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется."));

        courierId = courierClient.loginAndGetCourierId(uniqueLogin, "1234");
    }

    @Test
    @Step("Тест на создание курьера без пароля")
    public void testCannotCreateCourierWithoutPassword() {
        Response response = courierClient.createCourier(uniqueLogin, "", "kurier");
        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @Step("Тест на создание курьера без логина")
    public void testCannotCreateCourierWithoutLogin() {
        Response response = courierClient.createCourier("", "1234", "kurier");
        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @Step("Тест на создание курьера без имени (firstName)")
    public void testCannotCreateCourierWithoutFirstName() {
        Response response = courierClient.createCourier(uniqueLogin, "1234", "");

        if (response.statusCode() == 201) {
            int createdCourierId = courierClient.loginAndGetCourierId(uniqueLogin, "1234");
            deleteCourier(createdCourierId); // Удаляем созданного курьера
            throw new AssertionError("Ошибка: курьер был создан без обязательного поля firstName, что не соответствует требованиям.");
        } else {
            response.then()
                    .statusCode(400)
                    .body("message", equalTo("Недостаточно данных для создания учетной записи"));
        }
    }
}
