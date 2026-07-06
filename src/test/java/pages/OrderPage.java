package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class OrderPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public static final String URL = "https://qa-scooter.education-services.ru/order";

    // --- Шаг 1 "Для кого самокат" ---

    // Поле "Имя"
    private final By firstNameInput = By.cssSelector("input[placeholder='* Имя']");
    // Ошибка под полем "Имя"
    private final By firstNameError = By.cssSelector("input[placeholder='* Имя'] + div");

    // Поле "Фамилия"
    private final By lastNameInput = By.cssSelector("input[placeholder='* Фамилия']");
    // Ошибка под полем "Фамилия"
    private final By lastNameError = By.cssSelector("input[placeholder='* Фамилия'] + div");

    // Поле "Адрес"
    private final By addressInput = By.cssSelector("input[placeholder='* Адрес: куда привезти заказ']");

    // Поле "Станция метро"
    private final By metroInput = By.cssSelector("input[placeholder='* Станция метро']");
    // Варианты выпадающего списка станций метро
    private final By metroOptions = By.cssSelector(".select-search__option");
    // Ошибка под полем "Станция метро"
    private final By metroError = By.className("Order_MetroError__1BtZb");

    // Поле "Телефон"
    private final By phoneInput = By.cssSelector("input[placeholder='* Телефон: на него позвонит курьер']");
    // Ошибка под полем "Телефон"
    private final By phoneError = By.cssSelector("input[placeholder='* Телефон: на него позвонит курьер'] + div");

    // Кнопка "Далее"
    private final By nextButton = By.xpath("//button[text()='Далее']");

    // --- Шаг 2 "Про аренду" ---

    // Поле "Когда привезти самокат" (дата)
    private final By dateInput = By.cssSelector("input[placeholder='* Когда привезти самокат']");
    // Ячейки календаря, доступные для выбора
    private final By availableDatepickerDays = By.cssSelector(
            ".react-datepicker__day:not(.react-datepicker__day--disabled):not(.react-datepicker__day--outside-month)");

    // Выпадающий список "Срок аренды"
    private final By rentalPeriodControl = By.className("Dropdown-control");
    // Варианты срока аренды
    private final By rentalPeriodOptions = By.cssSelector(".Dropdown-menu .Dropdown-option");

    // Чекбокс цвета "чёрный жемчуг"
    private final By colorBlack = By.id("black");
    // Чекбокс цвета "серая безысходность"
    private final By colorGrey = By.id("grey");

    // Поле "Комментарий для курьера"
    private final By commentInput = By.cssSelector("input[placeholder='Комментарий для курьера']");

    // Кнопка "Заказать" (отправка заказа). На этой странице есть ещё одна кнопка
    // с текстом "Заказать" в шапке сайта, поэтому берём последнюю кнопку в блоке кнопок формы.
    private final By submitButton = By.cssSelector(".Order_Buttons__1xGrp button:last-child");

    // Всплывающее окно об успешном оформлении заказа
    private final By successPopup = By.cssSelector("[class*='Order_Modal']");

    public OrderPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public OrderPage open() {
        driver.get(URL);
        wait.until(ExpectedConditions.presenceOfElementLocated(firstNameInput));
        return this;
    }

    public OrderPage fillFirstStep(String firstName, String lastName, String address, String metroStation, String phone) {
        driver.findElement(firstNameInput).sendKeys(firstName);
        driver.findElement(lastNameInput).sendKeys(lastName);
        driver.findElement(addressInput).sendKeys(address);
        if (metroStation != null && !metroStation.isEmpty()) {
            selectMetroStation(metroStation);
        }
        driver.findElement(phoneInput).sendKeys(phone);
        return this;
    }

    private void selectMetroStation(String stationName) {
        WebElement input = driver.findElement(metroInput);
        input.click();
        input.sendKeys(stationName);
        List<WebElement> options = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(metroOptions));
        options.get(0).click();
    }

    public void clickNext() {
        driver.findElement(nextButton).click();
    }

    public OrderPage fillSecondStep(String rentalPeriod, By color, String comment) {
        pickFirstAvailableDate();
        selectRentalPeriod(rentalPeriod);
        driver.findElement(color).click();
        if (comment != null && !comment.isEmpty()) {
            driver.findElement(commentInput).sendKeys(comment);
        }
        return this;
    }

    private void pickFirstAvailableDate() {
        driver.findElement(dateInput).click();
        List<WebElement> days = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(availableDatepickerDays));
        days.get(0).click();
    }

    private void selectRentalPeriod(String periodText) {
        driver.findElement(rentalPeriodControl).click();
        List<WebElement> options = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(rentalPeriodOptions));
        for (WebElement option : options) {
            if (option.getText().equalsIgnoreCase(periodText)) {
                option.click();
                return;
            }
        }
        throw new IllegalArgumentException("Не найден срок аренды: " + periodText);
    }

    public void submitOrder() {
        WebElement button = driver.findElement(submitButton);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", button);
        wait.until(ExpectedConditions.elementToBeClickable(button)).click();
    }

    public String waitForSuccessPopupText() {
        WebElement popup = wait.until(ExpectedConditions.visibilityOfElementLocated(successPopup));
        return popup.getText();
    }

    // --- Проверки ошибок валидации шага 1 ---

    public boolean isFirstNameErrorDisplayed() {
        return driver.findElement(firstNameError).isDisplayed();
    }

    public boolean isLastNameErrorDisplayed() {
        return driver.findElement(lastNameError).isDisplayed();
    }

    public boolean isPhoneErrorDisplayed() {
        return driver.findElement(phoneError).isDisplayed();
    }

    public boolean isMetroErrorDisplayed() {
        return driver.findElement(metroError).isDisplayed();
    }

    public By getColorBlack() {
        return colorBlack;
    }

    public By getColorGrey() {
        return colorGrey;
    }
}
