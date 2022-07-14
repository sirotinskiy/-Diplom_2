package client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import model.request.User;

import static client.RestAssuredClient.getBaseSpec;
import static end_points.EndPoints.CREATE_USER;
import static io.restassured.RestAssured.given;

public class CreateUser {
    @Step("POST create user ")
    public static ValidatableResponse createUser(User user){
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .post(CREATE_USER)
                .then();
    }
}
