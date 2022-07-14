package changeInfoUser;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ValidatableResponse;
import model.request.User;
import model.response.changeUser.ResponseChangeInfoUser;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static client.ChangeInfoUser.changeInfoUser;
import static client.CreateUser.createUser;
import static client.DeleteUser.deleteUser;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Parameterized.class)
public class ChangeInfoUserTest {

    private final int expectedStatusCode;
    private final boolean expectedSuccess;
    private final User changeUser;

    private String token;
    private static User user = User.builder()
            .email(new StringBuilder(RandomStringUtils.randomAlphabetic(5) + "@ggmail.com").toString().toLowerCase())
            .password(RandomStringUtils.randomAlphabetic(5))
            .name(RandomStringUtils.randomAlphabetic(5)).build();

    public ChangeInfoUserTest(int expectedStatusCode, boolean expectedSuccess, User changeUser) {
        this.expectedStatusCode = expectedStatusCode;
        this.expectedSuccess = expectedSuccess;
        this.changeUser = changeUser;
    }

    @Parameterized.Parameters(name = "test data: {2}")
    public static Object[][] getNotValidData(){
        return new Object[][]{
                {SC_OK, true,
                User.builder()
                        .email(new StringBuilder(RandomStringUtils.randomAlphabetic(5) + "@ggmail.com").toString().toLowerCase())
                        .password(user.getPassword())
                        .name(user.getName())
                        .build()},
                {SC_OK, true,
                        User.builder()
                                .email(user.getEmail())
                                .password(RandomStringUtils.randomAlphabetic(5))
                                .name(user.getName())
                                .build()},
                {SC_OK, true,
                        User.builder()
                                .email(user.getEmail())
                                .password(user.getPassword())
                                .name(RandomStringUtils.randomAlphabetic(5))
                                .build()},
                {SC_OK, true,
                        User.builder()
                                .email(new StringBuilder(RandomStringUtils.randomAlphabetic(5) + "@ggmail.com").toString().toLowerCase())
                                .password(RandomStringUtils.randomAlphabetic(5))
                                .name(RandomStringUtils.randomAlphabetic(5))
                                .build()}
        };
    }

    @Before
    public void setUp() {
        ValidatableResponse validatableResponseCreateUser = createUser(user);
        token = validatableResponseCreateUser.extract().path("accessToken");
    }

    @After
    public void shutDown() {
        deleteUser(token);
    }

    @Test
    @DisplayName("Валидное изменение данных пользователя")
    public void changeInfoUserTest(){
        ValidatableResponse responseChangeInfo = changeInfoUser(changeUser, token);

        ResponseChangeInfoUser responseChangeInfoUser = responseChangeInfo.extract().as(new TypeRef<ResponseChangeInfoUser>(){
        }.getType());

        String actualName = responseChangeInfoUser.getUser().getName();
        String actualEmail = responseChangeInfoUser.getUser().getEmail();
        boolean actualSuccess = responseChangeInfoUser.getSuccess();

        responseChangeInfo.statusCode(expectedStatusCode);
        assertThat(changeUser.getName(), equalTo(actualName));
        assertThat(changeUser.getEmail(), equalTo(actualEmail));
        assertThat(expectedSuccess, equalTo(actualSuccess));
    }


}
