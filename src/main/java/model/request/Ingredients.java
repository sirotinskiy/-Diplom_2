package model.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Ingredients{
	@JsonProperty("ingredients")
	private List<String> ingredients;
}