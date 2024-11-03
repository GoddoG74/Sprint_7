package helpers;

import com.github.javafaker.Faker;
import java.util.List;

public class Order {
    private String firstName;
    private String lastName;
    private String address;
    private String metroStation;
    private String phone;
    private int rentTime;
    private String deliveryDate;
    private String comment;
    private List<String> color;

    private static final Faker faker = new Faker();

    // Конструктор для генерации данных с использованием Faker
    public Order(List<String> color) {
        this.firstName = faker.name().firstName();
        this.lastName = faker.name().lastName();
        this.address = faker.address().streetAddress();
        this.metroStation = String.valueOf(faker.number().numberBetween(1, 10));
        this.phone = faker.phoneNumber().phoneNumber();
        this.rentTime = faker.number().numberBetween(1, 10);
        this.deliveryDate = "2023-06-06";
        this.comment = "Sample comment";
        this.color = color;
    }

    // Пустой конструктор, если требуется Jackson
    public Order() {
    }

    // Геттеры и сеттеры для всех полей
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMetroStation() {
        return metroStation;
    }

    public void setMetroStation(String metroStation) {
        this.metroStation = metroStation;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getRentTime() {
        return rentTime;
    }

    public void setRentTime(int rentTime) {
        this.rentTime = rentTime;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<String> getColor() {
        return color;
    }

    public void setColor(List<String> color) {
        this.color = color;
    }
}
