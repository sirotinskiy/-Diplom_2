package getUserOrders;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ValidatableResponse;
import model.request.Ingredients;
import model.request.User;
import model.response.ingredients.DataItem;
import model.response.ingredients.ResponseIngredients;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static client.CreateOrder.createOrder;
import static client.CreateUser.createUser;
import static client.DeleteUser.deleteUser;
import static client.GetIngredient.getIngredients;
import static client.GetUserOrders.getUserOrders;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class GetUserOrdersTest {

    private List<String> idIngredientsList;
    private ResponseIngredients responseIngredients;

    private User user;
    private String token;

    @Before
    public void setUp(){
        user = User.builder()
                .email(new StringBuilder(RandomStringUtils.randomAlphabetic(5) + "@ggmail.com").toString().toLowerCase())
                .password(RandomStringUtils.randomAlphabetic(5))
                .name(RandomStringUtils.randomAlphabetic(5)).build();
        ValidatableResponse responseUser = createUser(user);

        token = responseUser.extract().path("accessToken");

        responseIngredients = getIngredients().extract().as(new TypeRef<ResponseIngredients>() {
        }.getType());

        idIngredientsList = responseIngredients.getData()
                .stream()
                .map(DataItem::getId)
                .collect(Collectors.toList());
    }

    @After
    public void shutUp(){
        deleteUser(token);
    }

    @Test
    @DisplayName("Получение заказов клиента")
    public void getUserOrdersWithAuthorization(){
        Ingredients ingredients = Ingredients.builder()
                .ingredients(idIngredientsList)
                .build();
        createOrder(ingredients, token);

        ValidatableResponse validatableResponseGetOrder = getUserOrders(token);

        boolean expectSuccess = true;
        List<String> expectOrder = validatableResponseGetOrder.extract().path("orders");

        boolean actualSuccess = validatableResponseGetOrder.extract().path("success");
        Integer actualTotal = validatableResponseGetOrder.extract().path("total");


        validatableResponseGetOrder.statusCode(SC_OK);
        assertThat(expectSuccess, equalTo(actualSuccess));
        assertThat(expectOrder, notNullValue());
        assertThat(actualTotal, notNullValue());
    }

    @Test
    @DisplayName("Получение заказов без авторизации")
    public void getUserOrdersWithoutAuthorization(){
        Ingredients ingredients = Ingredients.builder()
                .ingredients(idIngredientsList)
                .build();
        ValidatableResponse validatableResponseOrder = createOrder(ingredients, "token");

        ValidatableResponse validatableResponseGetOrder = getUserOrders("");

        boolean expectSuccess = false;
        String expectedMessage = "You should be authorised";

        boolean actualSuccess = validatableResponseGetOrder.extract().path("success");
        String actualMessage = validatableResponseGetOrder.extract().path("message");

        validatableResponseGetOrder.statusCode(SC_UNAUTHORIZED);
        assertThat(expectSuccess, equalTo(actualSuccess));
        assertThat(expectedMessage, equalTo(actualMessage));
    }


}
