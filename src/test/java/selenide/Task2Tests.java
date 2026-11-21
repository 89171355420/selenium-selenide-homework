package selenide;

import api.ApiData;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.confirm;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.refresh;

class Task2Tests extends BaseSelenideTest {

    @Test
    void addProductInAdminAndCheckStorefront() {
        String name = ApiData.randomName("Товар Selenide");

        loginAsAdmin();
        $("#n-name").setValue(name);
        $("#n-price").setValue("150");
        $("#add-btn").click();
        $$("div.toast").findBy(text("Товар успешно добавлен!")).shouldBe(visible);

        open("/");
        $$(".product-card h4").findBy(text(name)).shouldBe(visible);
    }

    @Test
    void addedProductIsVisibleInCart() {
        String name = ApiData.randomName("Товар в корзину");
        ApiData.createProduct(name, 100);

        open("/");
        addToCart(name);
        openCart();

        $("#cart-items").shouldHave(text(name));
        $$("#cart-items .cart-item").shouldHave(size(1));
    }

    @Test
    void adminLoginWithWrongCredentialsShowsError() {
        open("/login");
        $("#username").setValue("wrong_user");
        $("#password").setValue("wrong_password");
        $("form.login-form button[type='submit']").click();

        $(".alert-danger").shouldBe(visible);
    }

    // по заданию тест должен упасть: стенд не сохраняет корзину после обновления страницы
    @Test
    void cartShouldBeSavedAfterPageRefresh() {
        String name = ApiData.randomName("Товар после обновления");
        ApiData.createProduct(name, 100);

        open("/");
        addToCart(name);
        openCart();
        $("#cart-items").shouldHave(text(name));

        refresh();
        openCart();
        $("#cart-items").shouldHave(text(name));
    }

    @Test
    void alertWhenOrderTotalExceedsLimit() {
        String name = ApiData.randomName("Дорогой товар");
        ApiData.createProduct(name, 400);

        open("/");
        addToCart(name);
        openCart();
        $("#makeOrder").click();

        confirm("[SmartShop]: Денег не хватает! Сумма 400 ₽ превышает лимит 300 ₽.");
    }
}
