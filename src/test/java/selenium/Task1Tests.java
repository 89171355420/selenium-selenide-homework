package selenium;

import api.ApiData;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Task1Tests extends BaseSeleniumTest {

    @Test
    void addProductInAdminAndCheckStorefront() {
        String name = ApiData.randomName("Товар Selenium");

        loginAsAdmin();
        addProductInAdmin(name, "150");

        openPage("/");
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'product-card')][.//h4[normalize-space()='" + name + "']]")));

        List<String> names = getProductNames().stream().map(WebElement::getText).toList();
        assertTrue(names.contains(name), "Товар должен отображаться на витрине. Найдено: " + names);
    }

    @Test
    void addedProductIsVisibleInCart() {
        String name = ApiData.randomName("Товар в корзину");
        ApiData.createProduct(name, 100);

        openPage("/");
        addToCart(name);
        openCart();

        WebElement cartProductName = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@id='cart-items']//div[contains(@class,'cart-item')]//b[normalize-space()='"
                        + name + "']")));
        assertEquals(name, cartProductName.getText());
    }

    @Test
    void adminLoginWithWrongCredentialsShowsError() {
        openPage("/login");
        driver.findElement(By.id("username")).sendKeys("wrong_user");
        driver.findElement(By.id("password")).sendKeys("wrong_password");
        driver.findElement(By.cssSelector("form.login-form button[type='submit']")).click();

        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert-danger")));
        String errorText = error.getText();
        assertTrue(errorText.contains("Неверные учетные данные") || errorText.contains("Invalid credentials"),
                "Ожидалось сообщение о неверных учётных данных, было: " + errorText);
    }

    // по заданию тест должен упасть: стенд не сохраняет корзину после обновления страницы
    @Test
    void cartShouldBeSavedAfterPageRefresh() {
        String name = ApiData.randomName("Товар после обновления");
        ApiData.createProduct(name, 100);

        openPage("/");
        addToCart(name);
        openCart();
        wait.until(ExpectedConditions.visibilityOfElementLocated(cartItem(name)));

        driver.navigate().refresh();
        openCart();

        boolean cartIsSaved = !driver.findElements(By.cssSelector("#cart-items .cart-item")).isEmpty();
        assertTrue(cartIsSaved, "Товар должен сохраниться в корзине после обновления страницы");
    }

    private static By cartItem(String productName) {
        return By.xpath("//div[@id='cart-items']//div[contains(@class,'cart-item')]//b[normalize-space()='"
                + productName + "']");
    }
}
