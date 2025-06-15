package com.peter.tanxuannewapp.domain.resposne;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.peter.tanxuannewapp.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ResLoginDTO {
    @JsonProperty("access_token")
    private String accessToken;
    private UserLogin user;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserLogin {
        private int id;
        private String email;
        private String name;
        private Role role;
    }

    @Getter
    @Setter
    public static class UserInsideToken{
        private int id;
        private String email;
        private String name;
    }

    @Getter
    @Setter
    public static class UserAccount{
        private UserLogin user;
    }
}
