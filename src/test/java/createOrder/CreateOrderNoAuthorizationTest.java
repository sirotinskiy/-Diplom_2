package createOrder;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ValidatableResponse;
import model.request.Ingredients;
import model.response.ingredients.DataItem;
import model.response.ingredients.ResponseIngredients;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static client.CreateOrder.createOrder;
import static client.GetIngredient.getIngredients;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class CreateOrderNoAuthorizationTest {

    private List<String> idIngredientsList;
    private ResponseIngredients responseIngredients;


    @Before
    public void setUp(){
        responseIngredients = getIngredients().extract().as(new TypeRef<ResponseIngredients>() {
        }.getType());

        idIngredientsList = responseIngredients.getData()
                .stream()
                .map(DataItem::getId)
                .collect(Collectors.toList());
    }

    @Test
    @DisplayName("Создание заказа без авторизации, но со всеми ингредиентами")
    public void createOrderWithIngredients(){
        Ingredients ingredients = Ingredients.builder()
                .ingredients(idIngredientsList)
                .build();
        ValidatableResponse validatableResponseOrder = createOrder(ingredients, "");

        boolean expectedSuccess = true;

        boolean actualSuccess = validatableResponseOrder.extract().path("success");
        String actualName = validatableResponseOrder.extract().path("name");
        Integer actualNumber = validatableResponseOrder.extract().path("order.number");

        validatableResponseOrder.statusCode(SC_OK);
        assertThat(expectedSuccess, equalTo(actualSuccess));
        assertThat(actualName, notNullValue());
        assertThat(actualNumber, notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации, без ингредиентов")
    public void createOrderWithoutIngredients(){
        Ingredients ingredients = Ingredients.builder()
                .ingredients(List.of())
                .build();
        ValidatableResponse validatableResponseOrder = createOrder(ingredients, "");

        boolean expectedSuccess = false;
        String expectedMessage = "Ingredient ids must be provided";

        boolean actualSuccess = validatableResponseOrder.extract().path("success");
        String actualMessage = validatableResponseOrder.extract().path("message");

        validatableResponseOrder.statusCode(SC_BAD_REQUEST);
        assertThat(expectedSuccess, equalTo(actualSuccess));
        assertThat(expectedMessage, equalTo(actualMessage));
    }

    @Test
    @DisplayName("Создание заказа без авторизации, с неверным хешем ингредиента")
    public void createOrderWithWrongIngredients(){
        Ingredients ingredients = Ingredients.builder()
                .ingredients(List.of("s,dfhbsmavfjkvfjhsdvakjhfgsakjhdfgkjsahgdfkjhagsdf"))
                .build();
        ValidatableResponse validatableResponseOrder = createOrder(ingredients, "");
        validatableResponseOrder.statusCode(SC_INTERNAL_SERVER_ERROR);
    }
}
