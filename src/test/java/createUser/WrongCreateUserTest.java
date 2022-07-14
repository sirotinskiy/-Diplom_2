package createUser;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.request.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static client.CreateUser.createUser;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(Parameterized.class)
public class WrongCreateUserTest {

    private final User user;
    private final int expectStatusCode;
    private final String expectErrorMessage;

    private final boolean expectSuccess;

    public WrongCreateUserTest(int expectStatusCode, boolean expectSuccess, String expectErrorMessage, User user) {
        this.user = user;
        this.expectStatusCode = expectStatusCode;
        this.expectErrorMessage = expectErrorMessage;
        this.expectSuccess = expectSuccess;
    }

    @Parameterized.Parameters(name = "test data: {3}")
    public static Object[][] getNotValidUser() {
        return new Object[][]{
                {SC_FORBIDDEN, false, "Email, password and name are required fields",
                        User.builder().email("")
                                .password(RandomStringUtils.randomAlphabetic(5))
                                .name(RandomStringUtils.randomAlphabetic(5))
                                .build()},
                {SC_FORBIDDEN, false, "Email, password and name are required fields",
                        User.builder().email(new StringBuilder(RandomStringUtils.randomAlphabetic(5) + "@ggmail.com").toString())
                                .password("")
                                .name(RandomStringUtils.randomAlphabetic(5))
                                .build()},
                {SC_FORBIDDEN, false, "Email, password and name are required fields",
                        User.builder().email(new StringBuilder(RandomStringUtils.randomAlphabetic(5) + "@ggmail.com").toString())
                                .password(RandomStringUtils.randomAlphabetic(5))
                                .name("")
                                .build()}
        };
    }

    @Test
    @DisplayName("Невалидное создание пользователя")
    public void createUserWithData() {
        ValidatableResponse response = createUser(user);
        response.statusCode(expectStatusCode);
        response.body("success", equalTo(expectSuccess));
        response.body("message", equalTo(expectErrorMessage));
    }
}
