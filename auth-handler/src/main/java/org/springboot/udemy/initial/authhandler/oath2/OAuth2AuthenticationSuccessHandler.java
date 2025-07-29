package org.springboot.udemy.initial.authhandler.oath2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springboot.udemy.initial.authhandler.enums.AppRoleCategory;
import org.springboot.udemy.initial.authhandler.enums.AuthProvider;
import org.springboot.udemy.initial.authhandler.kafkaConfig.KafkaProducerService;
import org.springboot.udemy.initial.authhandler.kafkaConfig.UserCreatedEvent;
import org.springboot.udemy.initial.authhandler.model.Role;
import org.springboot.udemy.initial.authhandler.model.User;
import org.springboot.udemy.initial.authhandler.repository.RoleRepository;
import org.springboot.udemy.initial.authhandler.repository.UserRepository;
import org.springboot.udemy.initial.authhandler.security.JwtUtil;
import org.springboot.udemy.initial.authhandler.springUser.model.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    KafkaProducerService kafkaProducerService;

    public OAuth2AuthenticationSuccessHandler(UserRepository userRepository, RoleRepository roleRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauthUser = oauthToken.getPrincipal();

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        // You can detect provider here if needed
        String registrationId = oauthToken.getAuthorizedClientRegistrationId().toLowerCase();
        AuthProvider provider = switch (registrationId) {
            case "google" -> AuthProvider.GOOGLE;
            case "github" -> AuthProvider.GITHUB;
            default -> AuthProvider.OAUTH2;
        };

        log.info("OAuth2 Login Success: provider={}, email={}, name={}", provider, email, name);

        // Try to find existing user or create a new one
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> registerNewUser(email, name, provider));

        Set<String> roleNames = user.getRoles().stream()
                .map(role -> role.getAppRoleCategory().name())
                .collect(Collectors.toSet());


//        Config for Kafka
        UserCreatedEvent event = new UserCreatedEvent(
                user.getUserId().toString(),
                user.getUserName(),
                user.getEmail(),
                roleNames,
                user.getAuthProvider().name()
        );

        log.info(event.getUsername());

        kafkaProducerService.publishUserCreatedEvent(event);

        // Generate JWT for the user
        String jwt = null;
        try {
            jwt = jwtUtil.generateJWT(UserDetailsImpl.build(user));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        response.setContentType("application/json");
        response.getWriter().write("{\"token\": \"" + jwt + "\"}");
        response.getWriter().flush();
    }

    private User registerNewUser(String email, String name, AuthProvider provider) {
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setUserName(name != null && !name.isBlank() ? name : email.split("@")[0]);
        newUser.setAuthProvider(provider);

        // Assign default ROLE_USER
        Role role = roleRepository.findRoleByAppRoleCategory(AppRoleCategory.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Default ROLE_USER not found"));;

        newUser.setRoles(Set.of(role));


        return userRepository.save(newUser);
    }

}
