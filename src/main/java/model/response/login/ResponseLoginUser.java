package model.response.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseLoginUser{

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("accessToken")
    private String accessToken;

    @JsonProperty("user")
    private UserResponse user;

    @JsonProperty("refreshToken")
    private String refreshToken;

}