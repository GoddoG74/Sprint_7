package tests;

import helpers.CourierClient;
import io.qameta.allure.Description;
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
    private String password;
    private String firstName;

    @Before
    @Step("Инициализация данных перед тестом")
    public void setUp() {
        courierClient = new CourierClient();
        uniqueLogin = CourierClient.generateUniqueLogin();
        password = CourierClient.generatePassword();
        firstName = CourierClient.generateFirstName();
    }

    @After
    @Step("Очистка данных после теста")
    public void tearDown() {
        if (courierId != 0) {
            deleteCourier(courierId);
        }
    }

    @Step("Удаление курьера с ID: {courierId}")
    private void deleteCourier(int courierId) {
        Response deleteResponse = courierClient.deleteCourier(courierId);
        if (deleteResponse.getStatusCode() != 200) {
            System.err.println("Ошибка при удалении курьера. Статус: " + deleteResponse.getStatusCode());
        }
    }

    @Test
    @Description("Проверка успешного создания курьера")
    @Step("Тест на успешное создание курьера")
    public void testCreateCourierSuccessfully() {
        Response response = courierClient.createCourier(uniqueLogin, password, firstName);
        response.then()
                .statusCode(201)
                .body("ok", equalTo(true));

        courierId = courierClient.loginAndGetCourierId(uniqueLogin, password);
    }

    @Test
    @Description("Проверка ошибки при создании дублирующего курьера")
    @Step("Тест на предотвращение создания дублирующего курьера")
    public void testCannotCreateDuplicateCourier() {
        Response createResponse = courierClient.createCourier(uniqueLogin, password, firstName);
        createResponse.then().statusCode(201);

        courierId = courierClient.loginAndGetCourierId(uniqueLogin, password);

        Response duplicateResponse = courierClient.createCourier(uniqueLogin, password, firstName);
        duplicateResponse.then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется."));
    }

    @Test
    @Description("Проверка ошибки при создании курьера без пароля")
    @Step("Тест на создание курьера без пароля")
    public void testCannotCreateCourierWithoutPassword() {
        Response response = courierClient.createCourier(uniqueLogin, "", firstName);
        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @Description("Проверка ошибки при создании курьера без логина")
    @Step("Тест на создание курьера без логина")
    public void testCannotCreateCourierWithoutLogin() {
        Response response = courierClient.createCourier("", password, firstName);
        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @Description("Проверка ошибки при создании курьера без имени (firstName)")
    @Step("Тест на создание курьера без имени (firstName)")
    public void testCannotCreateCourierWithoutFirstName() {
        Response response = courierClient.createCourier(uniqueLogin, password, "");

        if (response.statusCode() == 201) {
            int createdCourierId = courierClient.loginAndGetCourierId(uniqueLogin, password);
            deleteCourier(createdCourierId);
            throw new AssertionError("Ошибка: курьер был создан без обязательного поля firstName, что не соответствует требованиям.");
        } else {
            response.then()
                    .statusCode(400)
                    .body("message", equalTo("Недостаточно данных для создания учетной записи"));
        }
    }
}
