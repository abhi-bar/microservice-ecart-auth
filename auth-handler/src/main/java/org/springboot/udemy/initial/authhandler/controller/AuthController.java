package org.springboot.udemy.initial.authhandler.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springboot.udemy.initial.authhandler.enums.AppRoleCategory;
import org.springboot.udemy.initial.authhandler.enums.AuthProvider;
import org.springboot.udemy.initial.authhandler.kafkaConfig.KafkaProducerService;
import org.springboot.udemy.initial.authhandler.kafkaConfig.UserCreatedEvent;
import org.springboot.udemy.initial.authhandler.model.Role;
import org.springboot.udemy.initial.authhandler.model.User;
import org.springboot.udemy.initial.authhandler.repository.RoleRepository;
import org.springboot.udemy.initial.authhandler.repository.UserRepository;
import org.springboot.udemy.initial.authhandler.request.LoginRequest;
import org.springboot.udemy.initial.authhandler.request.SignupRequest;
import org.springboot.udemy.initial.authhandler.response.MessageResponse;
import org.springboot.udemy.initial.authhandler.response.UserInfoResponse;
import org.springboot.udemy.initial.authhandler.security.JwtUtil;
import org.springboot.udemy.initial.authhandler.springUser.model.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    KafkaProducerService kafkaProducerService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) throws Exception {
        Authentication authentication;
        try{
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException authenticationException){
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad Credentials");
            map.put("status", false);
            return new ResponseEntity<Object>(map, HttpStatus.NOT_ACCEPTABLE);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwtToken = jwtUtil.generateJWT(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

//        returning empty
        log.info(roles.toString());

        UserInfoResponse response = new UserInfoResponse(userDetails.getId(),
                userDetails.getUsername(),roles,jwtToken);

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest){
        if(userRepository.existsUserByUserName(signupRequest.getUsername())){
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username alrady exist"));
        }

        if (userRepository.existsUserByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        User user = new User(signupRequest.getUsername(),
                signupRequest.getEmail(),
                passwordEncoder.encode(signupRequest.getPassword()));

        // Explicitly set provider
        user.setAuthProvider(AuthProvider.LOCAL);

        Set<String> rolesSet = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if(rolesSet==null){
            Role userRole = roleRepository.findRoleByAppRoleCategory(AppRoleCategory.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
            roles.add(userRole);
        } else {
            rolesSet.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findRoleByAppRoleCategory(AppRoleCategory.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "seller":
                        Role modRole = roleRepository.findRoleByAppRoleCategory(AppRoleCategory.ROLE_SELLER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findRoleByAppRoleCategory(AppRoleCategory.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

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

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }


    @GetMapping("/username")
    public String currentUserName(Authentication authentication){
        if (authentication != null)
            return authentication.getName();
        else
            return "";
    }


    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(Authentication authentication){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        UserInfoResponse response = new UserInfoResponse(userDetails.getId(),
                userDetails.getUsername(), roles);

        return ResponseEntity.ok().body(response);
    }




// Since jwtToken is passed as string
//    signout will take place from client/frontend side
    
}