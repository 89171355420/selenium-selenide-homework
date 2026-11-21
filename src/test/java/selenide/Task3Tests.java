package selenide;

import api.ApiData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;

class Task3Tests extends BaseSelenideTest {

    @AfterEach
    void deleteTestData() {
        ApiData.deleteAllProducts();
    }

    @Test
    void payForThreeUnitsOfProduct() {
        String name = ApiData.randomName("Товар за 100");
        ApiData.createProduct(name, 100);

        open("/");
        addToCart(name, 3);
        openCart();

        $("#cart-count").shouldHave(text("3"));
        $("#total-price").shouldHave(text("300"));

        $("#makeOrder").click();
        $$(".toast").findBy(text("Заказ принят в обработку!")).shouldBe(visible);
    }

    @Test
    void totalPriceForDifferentProducts() {
        String first = ApiData.randomName("Товар 200");
        String second = ApiData.randomName("Товар 50");
        String third = ApiData.randomName("Товар 25");
        ApiData.createProduct(first, 200);
        ApiData.createProduct(second, 50);
        ApiData.createProduct(third, 25);

        open("/");
        addToCart(first);
        addToCart(second);
        addToCart(third);
        openCart();

        $$("#cart-items .cart-item").shouldHave(size(3));
        $("#total-price").shouldHave(text("275"));
    }

    @Test
    void adminAddProductShowsNotification() {
        String name = ApiData.randomName("Новый товар админ");

        loginAsAdmin();
        $("#n-name").setValue(name);
        $("#n-price").setValue("80");
        $("#add-btn").click();

        $$("div.toast").findBy(text("Товар успешно добавлен!")).shouldBe(visible);
    }

    @Test
    void adminEditProductChangesStorefront() {
        String oldName = ApiData.randomName("Старое имя");
        int id = ApiData.createProduct(oldName, 60);
        String newName = ApiData.randomName("Новое имя");

        loginAsAdmin();
        $("#nm-" + id).setValue(newName);
        $("button[data-action='update'][data-id='" + id + "']").click();
        $$("div.toast").findBy(text("обновлен")).shouldBe(visible);

        open("/");
        $$(".product-card h4").findBy(text(newName)).shouldBe(visible);
    }
}
