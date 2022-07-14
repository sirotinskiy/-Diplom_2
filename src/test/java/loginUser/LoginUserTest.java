package loginUser;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ValidatableResponse;
import model.request.User;
import model.response.login.ResponseLoginUser;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static client.CreateUser.createUser;
import static client.DeleteUser.deleteUser;
import static client.LoginUser.loginUser;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class LoginUserTest {

    private User user;

    private String token;

    private ResponseLoginUser responseLoginUser;

    @Before
    public void setUp(){
       user = User.builder()
                .email(new StringBuilder(RandomStringUtils.randomAlphabetic(5) + "@ggmail.com").toString().toLowerCase())
                .password(RandomStringUtils.randomAlphabetic(5))
                .name(RandomStringUtils.randomAlphabetic(5)).build();
       createUser(user);
    }

    @After
    public void shutUp(){
        token = responseLoginUser.getAccessToken();
        if(!token.equals(null)){
            deleteUser(token);
        }
    }

    @Test
    @DisplayName("Успешный вход в систему")
    public void successLogin(){
        ValidatableResponse responseLogin = loginUser(User.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .build());
        responseLogin.statusCode(SC_OK);

        responseLoginUser = loginUser(User.builder().email(user.getEmail()).password(user.getPassword()).build())
                .extract().as(new TypeRef<ResponseLoginUser>(){
                }.getType());
        assertThat(responseLoginUser.getUser().getEmail(), equalTo(user.getEmail()));
        assertThat(responseLoginUser.getUser().getName(), equalTo(user.getName()));
    }
}
