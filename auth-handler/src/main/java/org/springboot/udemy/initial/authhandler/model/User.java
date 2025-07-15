package org.springboot.udemy.initial.authhandler.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springboot.udemy.initial.authhandler.enums.AuthProvider;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @NotBlank
    @Size(max = 20)
    @Column(name = "username")
    private String userName;


    @Size(max = 50)
    @Column(name = "email")
    private String email;


    @Size(max = 120)
    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider")
    private AuthProvider authProvider = AuthProvider.LOCAL;



    // Constructor for local signup
    public User(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.authProvider = AuthProvider.LOCAL;
    }

    // Constructor for OAuth2 signup
    public User(String userName, String email, AuthProvider provider) {
        this.userName = userName;
        this.email = email;
        this.authProvider = provider;
    }

    @Setter
    @Getter
    @ManyToMany(cascade = {CascadeType.MERGE},
            fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();


    public static User oauthUser(String name, String email) {
        User user = new User(name, email, (String) null);
        user.setAuthProvider(AuthProvider.OAUTH2);
        return user;
    }

    public static User localUser(String name, String email, String password) {
        User user = new User(name, email, password);
        user.setAuthProvider(AuthProvider.LOCAL);
        return user;
    }
}