package changeInfoUser;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.request.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static client.ChangeInfoUser.changeInfoUser;
import static client.CreateUser.createUser;
import static client.DeleteUser.deleteUser;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ChangeInfoUserWithWrongDataTest {

    private String token;
    private static User user;


    @Before
    public void setUp() {
        user = User.builder()
                .email(new StringBuilder(RandomStringUtils.randomAlphabetic(5) + "@ggmail.com").toString().toLowerCase())
                .password(RandomStringUtils.randomAlphabetic(5))
                .name(RandomStringUtils.randomAlphabetic(5)).build();

        ValidatableResponse validatableResponseCreateUser = createUser(user);
        token = validatableResponseCreateUser.extract().path("accessToken");
    }

    @After
    public void shutDown() {
        deleteUser(token);
    }

    @Test
    @DisplayName("Изменение логина вне авторизованной зоне")
    public void changeInfoUserNoAuthorizationTest(){
        String emptyToken = "";

        User changeUser = User.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .name(RandomStringUtils.randomAlphabetic(5))
                .build();
        ValidatableResponse responseChangeInfo = changeInfoUser(changeUser, emptyToken);

        boolean expectedSuccess = false;
        String expectedMessage = "You should be authorised";

        boolean actualSuccess = responseChangeInfo.extract().path("success");
        String actualMessage = responseChangeInfo.extract().path("message");

        responseChangeInfo.statusCode(SC_UNAUTHORIZED);
        assertThat(expectedSuccess, equalTo(actualSuccess));
        assertThat(expectedMessage, equalTo(actualMessage));
    }

    @Test
    @DisplayName("Изменение логина на существующий логин")
    public void changeInfoUserWithRepeatingEmailTest(){
        User userWithRepeatingEmail = User.builder()
                .email(new StringBuilder(RandomStringUtils.randomAlphabetic(5) + "@ggmail.com").toString().toLowerCase())
                .password(RandomStringUtils.randomAlphabetic(5))
                .name(RandomStringUtils.randomAlphabetic(5)).build();

        ValidatableResponse validatableResponseCreateUser = createUser(userWithRepeatingEmail);
        String tokenUserWithRepeatingEmail = validatableResponseCreateUser.extract().path("accessToken");

        User changeUser = User.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .name(RandomStringUtils.randomAlphabetic(5))
                .build();

        ValidatableResponse responseChangeInfo = changeInfoUser(changeUser, tokenUserWithRepeatingEmail);

        boolean expectedSuccess = false;
        String expectedMessage = "User with such email already exists";

        boolean actualSuccess = responseChangeInfo.extract().path("success");
        String actualMessage = responseChangeInfo.extract().path("message");

        deleteUser(tokenUserWithRepeatingEmail);

        responseChangeInfo.statusCode(SC_FORBIDDEN);
        assertThat(expectedSuccess, equalTo(actualSuccess));
        assertThat(expectedMessage, equalTo(actualMessage));
    }
}
