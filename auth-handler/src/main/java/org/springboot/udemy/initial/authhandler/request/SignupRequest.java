package org.springboot.udemy.initial.authhandler.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class SignupRequest {

    @NotBlank(message = "Username is mandatory")
    @Size(min=3, max = 20)
    private String username;

    @NotBlank(message = "Email is mandatory")
    @Size(max = 200)
    @Email(message = "Needs to be a valid email")
    private String email;

    private Set<String> role;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

}
