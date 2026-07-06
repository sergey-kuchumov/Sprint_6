package tests;

import org.junit.jupiter.api.Test;
import pages.MainPage;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LogoTest extends BaseTest {

    @Test
    public void scooterLogoClickStaysOnMainPage() {
        MainPage mainPage = new MainPage(driver).open().acceptCookies();

        mainPage.clickScooterLogo();

        assertTrue(driver.getCurrentUrl().startsWith(MainPage.URL),
                "Ожидали остаться на главной странице, а оказались на " + driver.getCurrentUrl());
    }

    @Test
    public void yandexLogoOpensYandexInNewTab() {
        MainPage mainPage = new MainPage(driver).open().acceptCookies();
        Set<String> tabsBeforeClick = driver.getWindowHandles();

        mainPage.clickYandexLogo();

        String newTabUrl = mainPage.switchToNewTabAndGetUrl(tabsBeforeClick);
        assertTrue(newTabUrl.contains("yandex") || newTabUrl.contains("ya.ru"),
                "Ожидали, что новая вкладка ведёт на Яндекс, получили: " + newTabUrl);
    }
}
