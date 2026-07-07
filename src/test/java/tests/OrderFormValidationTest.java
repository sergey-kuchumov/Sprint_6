package tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import pages.OrderPage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderFormValidationTest extends BaseTest {

    private static final String VALID_FIRST_NAME = "Игорь";
    private static final String VALID_LAST_NAME = "Петров";
    private static final String VALID_ADDRESS = "Москва, ул. Пушкина, 1";
    private static final String VALID_METRO = "Бульвар Рокоссовского";
    private static final String VALID_PHONE = "+79261234567";

    private static String repeat(String s, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) sb.append(s);
        return sb.toString();
    }

    @ParameterizedTest(name = "Пустое поле \"{0}\" подсвечивается ошибкой")
    @ValueSource(strings = {"firstName", "lastName", "phone", "metro"})
    public void emptyRequiredFieldShowsValidationError(String emptyField) {
        OrderPage orderPage = new OrderPage(driver).open();

        orderPage.fillFirstStep(
                "firstName".equals(emptyField) ? "" : VALID_FIRST_NAME,
                "lastName".equals(emptyField) ? "" : VALID_LAST_NAME,
                VALID_ADDRESS,
                "metro".equals(emptyField) ? "" : VALID_METRO,
                "phone".equals(emptyField) ? "" : VALID_PHONE);
        orderPage.clickNext();

        switch (emptyField) {
            case "firstName":
                assertTrue(orderPage.isFirstNameErrorDisplayed(), "Ожидали ошибку под полем \"Имя\"");
                break;
            case "lastName":
                assertTrue(orderPage.isLastNameErrorDisplayed(), "Ожидали ошибку под полем \"Фамилия\"");
                break;
            case "phone":
                assertTrue(orderPage.isPhoneErrorDisplayed(), "Ожидали ошибку под полем \"Телефон\"");
                break;
            case "metro":
                assertTrue(orderPage.isMetroErrorDisplayed(), "Ожидали ошибку под полем \"Станция метро\"");
                break;
            default:
                throw new IllegalArgumentException("Неизвестное поле: " + emptyField);
        }
    }

    @ParameterizedTest(name = "Латиница в поле \"{0}\" подсвечивается ошибкой")
    @ValueSource(strings = {"firstName", "lastName"})
    public void latinLettersInNameShowValidationError(String field) {
        OrderPage orderPage = new OrderPage(driver).open();

        orderPage.fillFirstStep(
                "firstName".equals(field) ? "Igor" : VALID_FIRST_NAME,
                "lastName".equals(field) ? "Petrov" : VALID_LAST_NAME,
                VALID_ADDRESS,
                VALID_METRO,
                VALID_PHONE);
        orderPage.clickNext();

        switch (field) {
            case "firstName":
                assertTrue(orderPage.isFirstNameErrorDisplayed(), "Ожидали ошибку под полем \"Имя\" при вводе латиницы");
                break;
            case "lastName":
                assertTrue(orderPage.isLastNameErrorDisplayed(), "Ожидали ошибку под полем \"Фамилия\" при вводе латиницы");
                break;
            default:
                throw new IllegalArgumentException("Неизвестное поле: " + field);
        }
    }

    @Test
    public void phoneWithOnlySymbolsShowsValidationError() {
        OrderPage orderPage = new OrderPage(driver).open();

        orderPage.fillFirstStep(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_ADDRESS, VALID_METRO, "!@#$%^&*");
        orderPage.clickNext();

        assertTrue(orderPage.isPhoneErrorDisplayed(),
                "Ожидали ошибку под полем \"Телефон\" при вводе одних символов");
    }

    @Test
    public void phoneWithOnlyLettersShowsValidationError() {
        OrderPage orderPage = new OrderPage(driver).open();

        orderPage.fillFirstStep(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_ADDRESS, VALID_METRO, "абвгдежзик");
        orderPage.clickNext();

        assertTrue(orderPage.isPhoneErrorDisplayed(),
                "Ожидали ошибку под полем \"Телефон\" при вводе одних букв");
    }

    @ParameterizedTest(name = "Символы в поле \"{0}\" подсвечиваются ошибкой")
    @ValueSource(strings = {"firstName", "lastName", "address"})
    public void symbolsInFieldShowValidationError(String field) {
        OrderPage orderPage = new OrderPage(driver).open();

        orderPage.fillFirstStep(
                "firstName".equals(field) ? "!@#$%" : VALID_FIRST_NAME,
                "lastName".equals(field) ? "!@#$%" : VALID_LAST_NAME,
                "address".equals(field) ? "!@#$%^&*" : VALID_ADDRESS,
                VALID_METRO,
                VALID_PHONE);
        orderPage.clickNext();

        switch (field) {
            case "firstName":
                assertTrue(orderPage.isFirstNameErrorDisplayed(), "Ожидали ошибку под полем \"Имя\" при вводе символов");
                break;
            case "lastName":
                assertTrue(orderPage.isLastNameErrorDisplayed(), "Ожидали ошибку под полем \"Фамилия\" при вводе символов");
                break;
            case "address":
                assertTrue(orderPage.isAddressErrorDisplayed(), "Ожидали ошибку под полем \"Адрес\" при вводе символов");
                break;
            default:
                throw new IllegalArgumentException("Неизвестное поле: " + field);
        }
    }

    // Границы длины подтверждены вручную на реальном сайте: 1 символ - ошибка,
    // 2 и 15 символов - валидно, 16 символов - ошибка
    @ParameterizedTest(name = "Имя длиной {0} символов: ожидаем ошибку = {1}")
    @CsvSource({"1, true", "2, false", "15, false", "16, true"})
    public void firstNameLengthBoundary(int length, boolean expectError) {
        OrderPage orderPage = new OrderPage(driver).open();

        orderPage.fillFirstStep(repeat("а", length), VALID_LAST_NAME, VALID_ADDRESS, VALID_METRO, VALID_PHONE);
        orderPage.clickNext();

        assertEquals(expectError, orderPage.isFirstNameErrorDisplayed(),
                "Имя из " + length + " символов, ожидали ошибку=" + expectError);
    }

    // У фамилии верхней границы длины не обнаружено (проверено вручную до 50 символов) -
    // проверяем только минимальную длину
    @ParameterizedTest(name = "Фамилия длиной {0} символов: ожидаем ошибку = {1}")
    @CsvSource({"1, true", "2, false"})
    public void lastNameMinLength(int length, boolean expectError) {
        OrderPage orderPage = new OrderPage(driver).open();

        orderPage.fillFirstStep(VALID_FIRST_NAME, repeat("а", length), VALID_ADDRESS, VALID_METRO, VALID_PHONE);
        orderPage.clickNext();

        assertEquals(expectError, orderPage.isLastNameErrorDisplayed(),
                "Фамилия из " + length + " символов, ожидали ошибку=" + expectError);
    }

    @Test
    public void rentalPeriodRequiredToCompleteOrder() throws InterruptedException {
        OrderPage orderPage = new OrderPage(driver).open();

        orderPage.fillFirstStep(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_ADDRESS, VALID_METRO, VALID_PHONE);
        orderPage.clickNext();
        orderPage.fillSecondStepWithoutRentalPeriod(orderPage.getColorBlack());
        orderPage.submitOrder();
        // Негативная проверка: ждём немного, чтобы дать попапу шанс появиться, если бы
        // клик всё-таки сработал - дождаться "отсутствия" события штатным wait нельзя
        Thread.sleep(1000);

        assertFalse(orderPage.isSuccessPopupDisplayed(),
                "Без выбора срока аренды заказ не должен оформляться");
    }
}
