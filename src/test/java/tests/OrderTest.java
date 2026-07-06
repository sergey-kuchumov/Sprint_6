package tests;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import pages.MainPage;
import pages.OrderPage;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderTest extends BaseTest {

    private static Stream<Arguments> orderData() {
        return Stream.of(
                Arguments.of(
                        "top", "Игорь", "Петров", "Москва, ул. Пушкина, 1", "Бульвар Рокоссовского",
                        "+79261234567", "сутки", "black", "Домофон не работает, позвоните заранее"),
                Arguments.of(
                        "bottom", "Мария", "Иванова", "Санкт-Петербург, Невский проспект, 10", "Чистые пруды",
                        "+79161234567", "трое суток", "grey", "")
        );
    }

    @ParameterizedTest(name = "Заказ через кнопку \"{0}\": {1} {2}, {6}")
    @MethodSource("orderData")
    public void happyPathOrderShowsSuccessPopup(String entryPoint, String firstName, String lastName, String address,
                                                 String metroStation, String phone, String rentalPeriod,
                                                 String color, String comment) {
        MainPage mainPage = new MainPage(driver).open().acceptCookies();
        if ("top".equals(entryPoint)) {
            mainPage.clickOrderButtonTop();
        } else {
            mainPage.clickOrderButtonBottom();
        }

        OrderPage orderPage = new OrderPage(driver);
        By colorLocator = "black".equals(color) ? orderPage.getColorBlack() : orderPage.getColorGrey();

        orderPage.fillFirstStep(firstName, lastName, address, metroStation, phone);
        orderPage.clickNext();
        orderPage.fillSecondStep(rentalPeriod, colorLocator, comment);
        orderPage.submitOrder();

        String popupText = orderPage.waitForSuccessPopupText();
        assertTrue(popupText.toLowerCase().contains("заказ"),
                "Ожидали текст об успешном оформлении заказа, получили: " + popupText);
    }
}
