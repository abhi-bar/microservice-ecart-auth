package org.springboot.udemy.initial.authhandler.oath2;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springboot.udemy.initial.authhandler.enums.AppRoleCategory;
import org.springboot.udemy.initial.authhandler.enums.AuthProvider;
import org.springboot.udemy.initial.authhandler.kafkaConfig.KafkaProducerService;
import org.springboot.udemy.initial.authhandler.kafkaConfig.UserCreatedEvent;
import org.springboot.udemy.initial.authhandler.model.Role;
import org.springboot.udemy.initial.authhandler.model.User;
import org.springboot.udemy.initial.authhandler.repository.RoleRepository;
import org.springboot.udemy.initial.authhandler.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    KafkaProducerService kafkaProducerService;

    private static final Logger log = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("CustomOAuth2UserService.loadUser() called for provider: {}",
                userRequest.getClientRegistration().getRegistrationId());

        OAuth2User oauth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = new HashMap<>(oauth2User.getAttributes());


        log.info(attributes.values().stream().toList().toString());
//        log.info(attributes.keySet().stream().toList().toString());

        String registrationId = userRequest.getClientRegistration().getRegistrationId().toLowerCase();
        AuthProvider provider = switch (registrationId) {
            case "google" -> AuthProvider.GOOGLE;
            case "github" -> AuthProvider.GITHUB;
            default -> AuthProvider.OAUTH2;
        };

        String email = (String) attributes.get("email");
        String userName = null;


        log.info(email);

        if (email == null) {
            email = (String) attributes.get("login") + "@github.com";
            userName = (String) attributes.get("login");
            attributes.put("email", email); // important: set it so Spring doesn't fail
        } else {
            userName = (String) attributes.get("name");
            if (userName == null || userName.isBlank()) {
                userName = email.split("@")[0];
            }
        }

        // Check if user exists
        String finalUserName = userName;
        String finalEmail = email;
        User savedUser = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    log.info("Creating new user for email: {}", finalEmail);
                    Role defaultRole = roleRepository.findRoleByAppRoleCategory(AppRoleCategory.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Default role not found"));
                    User newUser = new User(finalUserName, finalEmail, provider);
                    newUser.setRoles(Set.of(defaultRole));
                    return userRepository.save(newUser);
                });

        log.info("User in DB after lookup/save: {}", savedUser.getEmail());

//      Setup for Kafka
        Set<String> roleNames = savedUser.getRoles().stream()
                .map(role -> role.getAppRoleCategory().name())
                .collect(Collectors.toSet());


//        Config for Kafka
        UserCreatedEvent event = new UserCreatedEvent(
                savedUser.getUserId().toString(),
                savedUser.getUserName(),
                savedUser.getEmail(),
                roleNames,
                savedUser.getAuthProvider().name()
        );

        log.info(event.getUsername());

//        kafkaProducerService.publishUserCreatedEvent(event);

        return new DefaultOAuth2User(
                Set.of(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "email"
        );
    }
}
