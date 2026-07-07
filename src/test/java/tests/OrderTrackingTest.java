package tests;

import org.junit.jupiter.api.Test;
import pages.MainPage;
import pages.OrderPage;
import pages.TrackPage;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderTrackingTest extends BaseTest {

    private static final String FIRST_NAME = "Игорь";
    private static final String LAST_NAME = "Петров";
    private static final String ADDRESS = "Москва, ул. Пушкина, 1";
    private static final String METRO_STATION = "Бульвар Рокоссовского";
    private static final String PHONE = "+79261234567";
    private static final String RENTAL_PERIOD = "сутки";
    private static final String COMMENT = "Домофон не работает, позвоните заранее";
    private static final String COLOR_NAME = "чёрный жемчуг";

    private static final Map<String, String> MONTHS_GENITIVE = Map.ofEntries(
            Map.entry("01", "января"), Map.entry("02", "февраля"), Map.entry("03", "марта"),
            Map.entry("04", "апреля"), Map.entry("05", "мая"), Map.entry("06", "июня"),
            Map.entry("07", "июля"), Map.entry("08", "августа"), Map.entry("09", "сентября"),
            Map.entry("10", "октября"), Map.entry("11", "ноября"), Map.entry("12", "декабря"));

    private static String toTrackDateFormat(String ddMmYyyy) {
        String[] parts = ddMmYyyy.split("\\.");
        int day = Integer.parseInt(parts[0]);
        return day + " " + MONTHS_GENITIVE.get(parts[1]);
    }

    private static class PlacedOrder {
        final TrackPage trackPage;
        final String deliveryDate;

        PlacedOrder(TrackPage trackPage, String deliveryDate) {
            this.trackPage = trackPage;
            this.deliveryDate = deliveryDate;
        }
    }

    private PlacedOrder placeOrderAndOpenTracking() {
        MainPage mainPage = new MainPage(driver).open().acceptCookies();
        mainPage.clickOrderButtonTop();

        OrderPage orderPage = new OrderPage(driver);
        orderPage.fillFirstStep(FIRST_NAME, LAST_NAME, ADDRESS, METRO_STATION, PHONE);
        orderPage.clickNext();
        orderPage.fillSecondStep(RENTAL_PERIOD, orderPage.getColorBlack(), COMMENT);
        String deliveryDate = orderPage.getSelectedDeliveryDate();
        orderPage.submitOrder();
        orderPage.waitForSuccessPopupText();

        return new PlacedOrder(orderPage.openTrackingPage(), deliveryDate);
    }

    @Test
    public void orderAttributesMatchSubmittedData() {
        PlacedOrder placedOrder = placeOrderAndOpenTracking();
        TrackPage trackPage = placedOrder.trackPage;

        assertAll(
                () -> assertEquals(FIRST_NAME, trackPage.getFieldValue("Имя")),
                () -> assertEquals(LAST_NAME, trackPage.getFieldValue("Фамилия")),
                () -> assertEquals(ADDRESS, trackPage.getFieldValue("Адрес")),
                () -> assertEquals(METRO_STATION, trackPage.getFieldValue("Станция метро")),
                () -> assertEquals(PHONE, trackPage.getFieldValue("Телефон")),
                () -> assertEquals(toTrackDateFormat(placedOrder.deliveryDate), trackPage.getFieldValue("Дата доставки")),
                () -> assertEquals(RENTAL_PERIOD, trackPage.getFieldValue("Срок аренды")),
                () -> assertEquals(COLOR_NAME, trackPage.getFieldValue("Цвет")),
                () -> assertEquals(COMMENT, trackPage.getFieldValue("Комментарий"))
        );
    }

    @Test
    public void cancelOrderShowsCancelledModal() {
        TrackPage trackPage = placeOrderAndOpenTracking().trackPage;

        trackPage.cancelOrder();

        String cancelledText = trackPage.waitForCancelledText();
        assertTrue(cancelledText.contains("Заказ отменён"),
                "Ожидали текст об отмене заказа, получили: " + cancelledText);
    }
}
