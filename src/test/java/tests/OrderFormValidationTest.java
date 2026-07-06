package tests;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pages.OrderPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderFormValidationTest extends BaseTest {

    private static final String VALID_FIRST_NAME = "Игорь";
    private static final String VALID_LAST_NAME = "Петров";
    private static final String VALID_ADDRESS = "Москва, ул. Пушкина, 1";
    private static final String VALID_METRO = "Бульвар Рокоссовского";
    private static final String VALID_PHONE = "+79261234567";

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
}
