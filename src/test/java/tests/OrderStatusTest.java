package tests;

import org.junit.jupiter.api.Test;
import pages.MainPage;
import pages.OrderStatusPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderStatusTest extends BaseTest {

    @Test
    public void nonExistentOrderNumberShowsNotFound() {
        new MainPage(driver).open().acceptCookies();
        OrderStatusPage statusPage = new OrderStatusPage(driver);

        statusPage.requestStatus("999999999");

        assertTrue(statusPage.isOrderNotFoundDisplayed(), "Ожидали блок об отсутствии заказа");
    }
}
