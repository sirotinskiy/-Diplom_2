package model.response.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;
}