package org.springboot.udemy.initial.authhandler.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

@Data
public class LoginRequest {

    @NotBlank
    private String username ;

    @NotBlank
    private String password;

//    Remove json ignore + to string exclude for debuging reasones
}
