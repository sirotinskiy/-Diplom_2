package client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import model.request.User;

import static client.RestAssuredClient.getBaseSpec;
import static end_points.EndPoints.LOGIN_USER;
import static io.restassured.RestAssured.given;

public class LoginUser {
    @Step("POST login user ")
    public static ValidatableResponse loginUser(User user){
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .post(LOGIN_USER)
                .then();
    }
}

