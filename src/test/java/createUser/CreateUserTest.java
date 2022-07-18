package createUser;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ValidatableResponse;
import model.request.User;
import model.response.login.ResponseLoginUser;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Test;

import static client.CreateUser.createUser;
import static client.DeleteUser.deleteUser;
import static client.LoginUser.loginUser;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;

public class CreateUserTest {

    private User user = User.builder()
            .email(new StringBuilder(RandomStringUtils.randomAlphabetic(5) + "@ggmail.com").toString())
            .password(RandomStringUtils.randomAlphabetic(5))
            .name(RandomStringUtils.randomAlphabetic(5)).build();
    private String token;

    @After
    public void shutDown(){
        ResponseLoginUser responseLoginUser = loginUser(User.builder().email(user.getEmail()).password(user.getPassword()).build())
                .extract().as(new TypeRef<ResponseLoginUser>(){
                }.getType());
        token = responseLoginUser.getAccessToken();
        if(!token.isEmpty()){
            deleteUser(token);
        }
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    public void createUniqueUser(){
        ValidatableResponse response = createUser(user);
        response.statusCode(SC_OK);
        response.body("success", equalTo(true));
    }

    @Test
    @DisplayName("Создание неуникального пользователя")
    public void createNotUniqueUser(){
        createUser(user);
        ValidatableResponse response = createUser(User.builder()
                .name(user.getName())
                .password(user.getPassword())
                .email(user.getEmail())
                .build());
        response.statusCode(SC_FORBIDDEN);
        response.body("success", equalTo(false));
        response.body("message", equalTo("User already exists"));
    }


}
