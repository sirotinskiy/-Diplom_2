package createOrder;

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
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class CreateOrderWithAuthorizationTest {

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
    public void shutUp() {
        deleteUser(token);
    }

    @Test
    @DisplayName("Создание заказа c авторизацией и со всеми ингредиентами")
    public void createOrderWithIngredients(){
        Ingredients ingredients = Ingredients.builder()
                .ingredients(idIngredientsList)
                .build();
        ValidatableResponse validatableResponseOrder = createOrder(ingredients, token);

        boolean expectedSuccess = true;
        String expectedName = validatableResponseOrder.extract().path("name");
        String expectedNameOwner = user.getName();
        String expectedStatus = "done";

        boolean actualSuccess = validatableResponseOrder.extract().path("success");
        String actualNameOwner = validatableResponseOrder.extract().path("order.owner.name");
        String actualStatus = validatableResponseOrder.extract().path("order.status");

        validatableResponseOrder.statusCode(SC_OK);
        assertThat(expectedSuccess, equalTo(actualSuccess));
        assertThat(expectedName, notNullValue());
        assertThat(expectedNameOwner, equalTo(actualNameOwner));
        assertThat(expectedStatus, equalTo(actualStatus));
    }

    @Test
    @DisplayName("Создание заказа c авторизацией и без ингредиентов")
    public void createOrderWithoutIngredients(){
        Ingredients ingredients = Ingredients.builder()
                .ingredients(List.of())
                .build();
        ValidatableResponse validatableResponseOrder = createOrder(ingredients, token);

        boolean expectedSuccess = false;
        String expectedMessage = "Ingredient ids must be provided";


        boolean actualSuccess = validatableResponseOrder.extract().path("success");
        String actualMessage = validatableResponseOrder.extract().path("message");

        validatableResponseOrder.statusCode(SC_BAD_REQUEST);
        assertThat(expectedSuccess, equalTo(actualSuccess));
        assertThat(expectedMessage,equalTo(actualMessage));
    }

    @Test
    @DisplayName("Создание заказа c авторизацией и не верным хешем ингредиента")
    public void createOrderWithWrongIngredients(){
        Ingredients ingredients = Ingredients.builder()
                .ingredients(List.of("skahdfkjhsgfkdjhgaskdjhfgksjahdgfkjhsagfjsadgjf"))
                .build();
        ValidatableResponse validatableResponseOrder = createOrder(ingredients, token);


        validatableResponseOrder.statusCode(SC_INTERNAL_SERVER_ERROR);
    }

}
