package model.response.changeUser;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResponseInfoUser {
	@JsonProperty("name")
	private String name;

	@JsonProperty("email")
	private String email;
}