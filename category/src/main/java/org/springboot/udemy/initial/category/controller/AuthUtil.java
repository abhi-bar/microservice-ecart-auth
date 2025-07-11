package org.springboot.udemy.initial.category.controller;

import com.embarkx.ecommerce.model.User;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthUtil {

    public User loggesInUser() {
        return new User();
    }

    public String loggedInEmail() {
        return "userEmail";
    }
}
