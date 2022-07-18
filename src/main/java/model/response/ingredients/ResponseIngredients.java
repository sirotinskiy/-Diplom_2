package model.response.ingredients;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResponseIngredients{

	@JsonProperty("data")
	private List<DataItem> data;

	@JsonProperty("success")
	private Boolean success;
}