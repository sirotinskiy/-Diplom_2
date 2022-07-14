package client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static client.RestAssuredClient.getBaseSpec;
import static end_points.EndPoints.GET_INGREDIENTS;
import static io.restassured.RestAssured.given;

public class GetIngredient {
    @Step("GET ingredients")
    public static ValidatableResponse getIngredients(){
        return given()
                .spec(getBaseSpec())
                .when()
                .get(GET_INGREDIENTS)
                .then();
    }

}

