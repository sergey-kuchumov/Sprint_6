package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Set;

public class MainPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public static final String URL = "https://qa-scooter.education-services.ru/";

    // Кнопка согласия с использованием cookie
    private final By cookieAcceptButton = By.id("rcc-confirm-button");

    // Логотип Яндекса (открывается в новой вкладке)
    private final By yandexLogo = By.className("Header_LogoYandex__3TSOI");

    // Логотип Самоката (ведёт на главную)
    private final By scooterLogo = By.className("Header_LogoScooter__3lsAR");

    // Кнопка "Заказать" вверху страницы
    private final By orderButtonTop = By.cssSelector(".Header_Nav__AGCXC button");

    // Кнопка "Заказать" внизу страницы
    private final By orderButtonBottom = By.cssSelector(".Home_FinishButton__1_cWm button");

    public MainPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public MainPage open() {
        driver.get(URL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("button")));
        return this;
    }

    public MainPage acceptCookies() {
        try {
            driver.findElement(cookieAcceptButton).click();
        } catch (Exception ignored) {
            // баннер мог уже быть закрыт или не появиться
        }
        return this;
    }

    public void clickOrderButtonTop() {
        driver.findElement(orderButtonTop).click();
    }

    public void clickOrderButtonBottom() {
        WebElement button = driver.findElement(orderButtonBottom);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", button);
        wait.until(ExpectedConditions.elementToBeClickable(button)).click();
    }

    public void clickScooterLogo() {
        driver.findElement(scooterLogo).click();
    }

    public void clickYandexLogo() {
        driver.findElement(yandexLogo).click();
    }

    public String switchToNewTabAndGetUrl(Set<String> handlesBeforeClick) {
        wait.until(ExpectedConditions.numberOfWindowsToBe(handlesBeforeClick.size() + 1));
        Set<String> handlesAfterClick = driver.getWindowHandles();
        handlesAfterClick.removeAll(handlesBeforeClick);
        String newTabHandle = handlesAfterClick.iterator().next();
        driver.switchTo().window(newTabHandle);
        wait.until(webDriver -> !webDriver.getCurrentUrl().equals("about:blank"));
        return driver.getCurrentUrl();
    }

    // --- FAQ "Вопросы о важном" ---

    private By faqQuestion(int index) {
        return By.id("accordion__heading-" + index);
    }

    private By faqAnswer(int index) {
        return By.id("accordion__panel-" + index);
    }

    public void openFaqQuestion(int index) {
        WebElement question = driver.findElement(faqQuestion(index));
        // Клик через JS, т.к. декоративная картинка самоката на странице может
        // перекрывать вопрос при обычном скролле и блокировать нативный клик.
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", question);
    }

    public String getFaqAnswerText(int index) {
        WebElement panel = wait.until(ExpectedConditions.visibilityOfElementLocated(faqAnswer(index)));
        return panel.getText();
    }
}
