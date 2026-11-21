package api;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public final class ApiData {

    public static final String BASE_URL = "http://localhost:8080";
    public static final String ADMIN_LOGIN = "admin";
    public static final String ADMIN_PASSWORD = "secret123";

    private ApiData() {
    }

    private static RequestSpecification authSpec() {
        return given()
                .baseUri(BASE_URL)
                .auth().basic(ADMIN_LOGIN, ADMIN_PASSWORD)
                .contentType(ContentType.JSON);
    }

    public static String randomName(String prefix) {
        return prefix + " " + System.nanoTime();
    }

    public static int createProduct(String name, double price) {
        return authSpec()
                .body(Map.of("name", name, "price", price))
                .post("/goods/add")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getInt("data.id");
    }

    public static void deleteProduct(int id) {
        authSpec()
                .delete("/goods/" + id)
                .then()
                .statusCode(200);
    }

    public static void deleteAllProducts() {
        List<Integer> ids = given()
                .baseUri(BASE_URL)
                .get("/goods/list?page=0&size=100")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("goods.id", Integer.class);

        if (ids != null) {
            for (Integer id : ids) {
                deleteProduct(id);
            }
        }
    }
}
