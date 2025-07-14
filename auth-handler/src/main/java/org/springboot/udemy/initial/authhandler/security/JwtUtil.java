package org.springboot.udemy.initial.authhandler.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springboot.udemy.initial.authhandler.springUser.model.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    @Value("${access-token-expiration-ms}")
    private Integer jwtExpiration;

    @Value("${refresh-token-expiration-ms}")
    private Integer refreshTokenExpiration;

    @Value("${private-key-path}")
    private String privateKeyPath;

//
////    Generate Jwt
//    public String generateJWT(UserDetailsImpl userDetails){
//        return Jwts.builder()
//                .setSubject(userDetails.getUsername())
//                .claim("roles",userDetails.getAuthorities().stream()
//                        .map(Object::toString)
//                        .toList())
//                .setIssuedAt(new Date())
//                .setExpiration(new Date((new Date()).getTime()+jwtExpiration))
//                .signWith(key())
//                .compact();
//    }

    //    Generate Jwt
    public String generateJWT(UserDetailsImpl user) throws Exception {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
    }

    // Not used every time once once
    public String generateRefreshToken(String username) throws Exception {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
    }


    // Reading key
    private RSAPrivateKey getPrivateKey() throws Exception {
        // If your privateKeyPath contains "classpath:", strip it first
        String path = privateKeyPath.startsWith("classpath:") ? privateKeyPath.substring(10) : privateKeyPath;

        try (InputStream is = new ClassPathResource(path).getInputStream()) {
            byte[] bytes = is.readAllBytes();

            String key = new String(bytes)
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            KeyFactory kf = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) kf.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(key)));
        }
    }

    private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

}
