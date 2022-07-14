package client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import model.request.User;

import static client.RestAssuredClient.getBaseSpec;
import static end_points.EndPoints.CHANGE_INFO_USER;
import static io.restassured.RestAssured.given;

public class ChangeInfoUser {
    @Step("PATCH change info user")
    public static ValidatableResponse changeInfoUser(User user, String token){
        return given()
                .spec(getBaseSpec())
                .header("authorization", token)
                .body(user)
                .when()
                .patch(CHANGE_INFO_USER)
                .then();
    }
}

