package model.response.ingredients;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DataItem{

	@JsonProperty("carbohydrates")
	private Integer carbohydrates;

	@JsonProperty("image")
	private String image;

	@JsonProperty("proteins")
	private Integer proteins;

	@JsonProperty("price")
	private Integer price;

	@JsonProperty("__v")
	private Integer V;

	@JsonProperty("name")
	private String name;

	@JsonProperty("fat")
	private Integer fat;

	@JsonProperty("_id")
	private String id;

	@JsonProperty("calories")
	private Integer calories;

	@JsonProperty("type")
	private String type;

	@JsonProperty("image_mobile")
	private String imageMobile;

	@JsonProperty("image_large")
	private String imageLarge;
}