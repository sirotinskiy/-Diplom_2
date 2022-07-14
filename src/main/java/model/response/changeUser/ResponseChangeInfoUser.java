package model.response.changeUser;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResponseChangeInfoUser {
	@JsonProperty("success")
	private Boolean success;

	@JsonProperty("user")
	private ResponseInfoUser user;
}