package org.springboot.udemy.initial.authhandler.config;

import jakarta.annotation.PostConstruct;
import org.springboot.udemy.initial.authhandler.enums.AppRoleCategory;
import org.springboot.udemy.initial.authhandler.enums.AuthProvider;
import org.springboot.udemy.initial.authhandler.model.Role;
import org.springboot.udemy.initial.authhandler.model.User;
import org.springboot.udemy.initial.authhandler.oath2.CustomOAuth2UserService;
import org.springboot.udemy.initial.authhandler.oath2.OAuth2AuthenticationSuccessHandler;
import org.springboot.udemy.initial.authhandler.repository.RoleRepository;
import org.springboot.udemy.initial.authhandler.repository.UserRepository;
import org.springboot.udemy.initial.authhandler.security.AuthEntryPointJwt;
import org.springboot.udemy.initial.authhandler.security.JwtUtil;
import org.springboot.udemy.initial.authhandler.springUser.service.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Set;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Bean
    public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        return new OAuth2AuthenticationSuccessHandler(userRepository, roleRepository, jwtUtil);
    }

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    UserDetailServiceImpl userDetailService;

    @Autowired
    private AuthEntryPointJwt authEntryPointJwt;

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authEntryPointJwt))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/h2-console/**").permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)   // <--- This saves the data
                        )
                        .successHandler(oAuth2AuthenticationSuccessHandler())
                )
                .formLogin(AbstractHttpConfigurer::disable);
        httpSecurity.authenticationProvider(daoAuthenticationProvider());
        httpSecurity.headers(headers -> headers.frameOptions(
                frameOptions -> frameOptions.sameOrigin()));

        return httpSecurity.build();
    }


    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Retrieve or create roles
            Role userRole = roleRepository.findRoleByAppRoleCategory(AppRoleCategory.ROLE_USER)
                    .orElseGet(() -> {
                        Role newUserRole = new Role(AppRoleCategory.ROLE_USER);
                        return roleRepository.save(newUserRole);
                    });

            Role sellerRole = roleRepository.findRoleByAppRoleCategory(AppRoleCategory.ROLE_SELLER)
                    .orElseGet(() -> {
                        Role newSellerRole = new Role(AppRoleCategory.ROLE_SELLER);
                        return roleRepository.save(newSellerRole);
                    });

            Role adminRole = roleRepository.findRoleByAppRoleCategory(AppRoleCategory.ROLE_ADMIN)
                    .orElseGet(() -> {
                        Role newAdminRole = new Role(AppRoleCategory.ROLE_ADMIN);
                        return roleRepository.save(newAdminRole);
                    });

            Set<Role> userRoles = Set.of(userRole);
            Set<Role> sellerRoles = Set.of(sellerRole);
            Set<Role> adminRoles = Set.of(userRole, sellerRole, adminRole);


            // Create users if not already present
            if (!userRepository.existsUserByUserName("user1")) {
                User user1 = new User("user1", "user1@example.com", passwordEncoder.encode("password1"));
                user1.setAuthProvider(AuthProvider.LOCAL);
                userRepository.save(user1);
            }

            if (!userRepository.existsUserByUserName("seller1")) {
                User seller1 = new User("seller1", "seller1@example.com", passwordEncoder.encode("password2"));
                seller1.setAuthProvider(AuthProvider.LOCAL);
                userRepository.save(seller1);
            }

            if (!userRepository.existsUserByUserName("admin")) {
                User admin = new User("admin", "admin@example.com", passwordEncoder.encode("adminPass"));
                admin.setAuthProvider(AuthProvider.LOCAL);
                userRepository.save(admin);
            }

            // Update roles for existing users
            userRepository.findByUserName("user1").ifPresent(user -> {
                user.setRoles(userRoles);
                userRepository.save(user);
            });

            userRepository.findByUserName("seller1").ifPresent(seller -> {
                seller.setRoles(sellerRoles);
                userRepository.save(seller);
            });

            userRepository.findByUserName("admin").ifPresent(admin -> {
                admin.setRoles(adminRoles);
                userRepository.save(admin);
            });
        };
    }
}
