package selenium;

import api.ApiData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public abstract class BaseSeleniumTest {

    protected static final String BASE_URL = ApiData.BASE_URL;
    protected static final String LOGIN = ApiData.ADMIN_LOGIN;
    protected static final String PASSWORD = ApiData.ADMIN_PASSWORD;

    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        ApiData.deleteAllProducts();
        driver = new ChromeDriver();
        driver.manage().window().setSize(new Dimension(1440, 900));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected void openPage(String path) {
        driver.get(BASE_URL + path);
    }

    protected void loginAsAdmin() {
        openPage("/login");
        driver.findElement(By.id("username")).sendKeys(LOGIN);
        driver.findElement(By.id("password")).sendKeys(PASSWORD);
        driver.findElement(By.cssSelector("form.login-form button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/admin"));
    }

    protected void addProductInAdmin(String name, String price) {
        driver.findElement(By.id("n-name")).sendKeys(name);
        driver.findElement(By.id("n-price")).sendKeys(price);
        driver.findElement(By.id("add-btn")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'toast')][contains(., 'Товар успешно добавлен')]")));
    }

    protected void addToCart(String productName) {
        WebElement card = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'product-card')][.//h4[normalize-space()='"
                        + productName + "']]")));
        card.findElement(By.cssSelector("button[data-action='add-to-cart']")).click();
    }

    protected void openCart() {
        driver.findElement(By.id("open-cart-btn")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cartModal")));
    }

    protected List<WebElement> getProductNames() {
        return driver.findElements(By.cssSelector("#products-list .product-card h4"));
    }
}
