package client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import model.request.Ingredients;

import static client.RestAssuredClient.getBaseSpec;
import static end_points.EndPoints.CREATE_ORDER;
import static io.restassured.RestAssured.given;

public class CreateOrder {

    @Step("POST create order")
    public static ValidatableResponse createOrder(Ingredients ingredients, String token) {
        return given()
                .spec(getBaseSpec())
                .header("authorization", token)
                .body(ingredients)
                .when()
                .post(CREATE_ORDER)
                .then();
    }
}
