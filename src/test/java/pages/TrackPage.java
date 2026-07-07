package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;

public class TrackPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Поле ввода номера заказа на странице отслеживания
    private final By searchInput = By.cssSelector("[class*='Track_Input']");
    // Кнопка "Посмотреть" рядом с полем ввода номера заказа
    private final By searchButton = By.xpath("//button[text()='Посмотреть']");

    // Строки с атрибутами заказа (название поля + значение)
    private final By rows = By.cssSelector("[class*='Track_Row']");
    private final By rowTitle = By.cssSelector("[class*='Track_Title']");
    private final By rowValue = By.cssSelector("[class*='Track_Value']");

    // Кнопка "Отменить заказ"
    private final By cancelButton = By.xpath("//button[text()='Отменить заказ']");

    // Всплывающее окно (используется и для успешного оформления, и для отмены заказа)
    private final By modal = By.cssSelector("[class*='Order_Modal']");

    public TrackPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // После перехода со страницы успешного оформления заказа номер в поле
    // уже подставлен приложением, поэтому просто ждём его появления и жмём "Посмотреть"
    public TrackPage openOrderInfo() {
        wait.until(driver -> !driver.findElement(searchInput).getAttribute("value").isEmpty());
        driver.findElement(searchButton).click();
        return this;
    }

    public String getFieldValue(String label) {
        List<WebElement> orderRows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(rows));
        for (WebElement row : orderRows) {
            if (row.findElement(rowTitle).getText().equals(label)) {
                return row.findElement(rowValue).getText();
            }
        }
        throw new NoSuchElementException("Поле не найдено на странице отслеживания заказа: " + label);
    }

    public void cancelOrder() {
        wait.until(ExpectedConditions.elementToBeClickable(cancelButton)).click();
        WebElement confirmModal = wait.until(ExpectedConditions.visibilityOfElementLocated(modal));
        List<WebElement> confirmButtons = confirmModal.findElements(By.cssSelector("button"));
        confirmButtons.get(1).click();
    }

    public String waitForCancelledText() {
        wait.until(driver -> driver.findElement(modal).getText().contains("Заказ отменён"));
        return driver.findElement(modal).getText();
    }
}
