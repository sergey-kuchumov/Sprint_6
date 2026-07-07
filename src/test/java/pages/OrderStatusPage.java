package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class OrderStatusPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Кнопка "Статус заказа" в шапке главной страницы
    private final By statusNavButton = By.className("Header_Link__1TAG7");

    // Поле ввода номера заказа в шапке
    private final By orderNumberInput = By.cssSelector("input[placeholder='Введите номер заказа']");

    // Кнопка "Go!"
    private final By goButton = By.xpath("//button[text()='Go!']");

    // Блок "заказ не найден" на странице статуса заказа
    private final By notFoundBlock = By.className("Track_NotFound__6oaoY");

    public OrderStatusPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void requestStatus(String orderNumber) {
        driver.findElement(statusNavButton).click();
        wait.until(ExpectedConditions.elementToBeClickable(orderNumberInput)).sendKeys(orderNumber);
        driver.findElement(goButton).click();
    }

    public boolean isOrderNotFoundDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(notFoundBlock)).isDisplayed();
    }
}
