package org.springboot.udemy.initial.authhandler.response;

import lombok.Data;

import java.util.List;

@Data
public class UserInfoSimpleResponse {

    private String token;
    public UserInfoSimpleResponse(String jwtToken) {
        this.token = jwtToken;
    }
}
