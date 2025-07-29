package org.springboot.udemy.initial.authhandler.kafkaConfig;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreatedEvent {
    private String userId;
    private String username;
    private String email;
    private Set<String> roles;
    private String authProvider;
}
