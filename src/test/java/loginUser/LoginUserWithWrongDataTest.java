package loginUser;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ValidatableResponse;
import model.request.User;
import model.response.login.ResponseLoginUser;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static client.CreateUser.createUser;
import static client.DeleteUser.deleteUser;
import static client.LoginUser.loginUser;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Parameterized.class)
public class LoginUserWithWrongDataTest {

    private static User user = User.builder()
            .email(new StringBuilder(RandomStringUtils.randomAlphabetic(5) + "@ggmail.com").toString().toLowerCase())
            .password(RandomStringUtils.randomAlphabetic(5))
            .name(RandomStringUtils.randomAlphabetic(5))
            .build();

    private final int expectStatusCode;
    private final boolean expectedSuccess;
    private final String expectErrorMessage;
    private final User loginUser;


    public LoginUserWithWrongDataTest(int expectStatusCode, boolean expectedSuccess, String expectErrorMessage, User loginUser) {
        this.expectStatusCode = expectStatusCode;
        this.expectedSuccess = expectedSuccess;
        this.expectErrorMessage = expectErrorMessage;
        this.loginUser = loginUser;
    }

    @Parameterized.Parameters(name = "test data: {3}")
    public static Object[][] getNotValidDataLogIn() {
        return new Object[][]{
                {SC_UNAUTHORIZED,false, "email or password are incorrect",
                User.builder()
                        .email(user.getEmail())
                        .password("")
                        .build()},
                {SC_UNAUTHORIZED,false, "email or password are incorrect",
                        User.builder()
                                .email("")
                                .password(user.getPassword())
                                .build()},
                {SC_UNAUTHORIZED,false, "email or password are incorrect",
                        User.builder()
                                .email("")
                                .password("")
                                .build()},
                {SC_UNAUTHORIZED, false, "email or password are incorrect",
                        User.builder()
                                .email(new StringBuilder(RandomStringUtils.randomAlphabetic(5) + "@ggmail.com").toString().toLowerCase())
                                .password(RandomStringUtils.randomAlphabetic(5))
                                .build()}
        };
    }

    @Before
    public void setUp(){
        createUser(user);
    }

    @After
    public void shutDown(){
        ResponseLoginUser responseLoginUser  = loginUser(User.builder().email(user.getEmail()).password(user.getPassword()).build())
                .extract().as(new TypeRef<ResponseLoginUser>(){
                }.getType());
        String token = responseLoginUser.getAccessToken();
        deleteUser(token);

    }

    @Test
    @DisplayName("Вход с невалидными данными")
    public void wrongLoginUser(){
        ValidatableResponse responseLogin = loginUser(loginUser);

        responseLogin.statusCode(expectStatusCode);

        boolean actualSuccess = responseLogin.extract().body().path("success");
        String actualMessage = responseLogin.extract().body().path("message");

        assertThat(expectedSuccess, equalTo(actualSuccess));
        assertThat(expectErrorMessage, equalTo(actualMessage));
    }
}
