package selenide;

import api.ApiData;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;

public abstract class BaseSelenideTest {

    protected static final String BASE_URL = ApiData.BASE_URL;

    @BeforeEach
    public void setUp() {
        ApiData.deleteAllProducts();
        Configuration.browser = "chrome";
        Configuration.baseUrl = BASE_URL;
        Configuration.browserSize = "1440x900";
        Configuration.timeout = 8000;
        open("/");
    }

    @AfterEach
    public void tearDown() {
        Selenide.closeWebDriver();
    }

    protected void loginAsAdmin() {
        open("/login");
        $("#username").setValue(ApiData.ADMIN_LOGIN);
        $("#password").setValue(ApiData.ADMIN_PASSWORD);
        $("form.login-form button[type='submit']").click();
        $("#add-btn").shouldBe(visible);
    }

    protected void addToCart(String productName) {
        $$(".product-card").findBy(text(productName))
                .find("button[data-action='add-to-cart']")
                .click();
    }

    protected void addToCart(String productName, int quantity) {
        var card = $$(".product-card").findBy(text(productName));
        card.find("input.qty-input").clear();
        card.find("input.qty-input").setValue(String.valueOf(quantity));
        card.find("button[data-action='add-to-cart']").click();
    }

    protected void openCart() {
        $("#open-cart-btn").click();
        $("#cart-items").shouldBe(visible);
    }
}
