package com.example.demo.Components;

import java.security.Key;
import java.util.Date;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

    private final Key jwtSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512); // Key length must be at least 32 bytes
    private final long jwtExpirationMs = 86400000; // Token validity (1 day)

    public String generateToken(String username) {
        String token = Jwts.builder()
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
        .signWith(jwtSecretKey, SignatureAlgorithm.HS512)
        .compact();

        System.out.println("Generated JWT Token: " + token);
        return token;

    }

    public String extractUsername(String token) {
        try {
            String username = Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
            System.out.println("Extracted username: " + username);
            return username;
        } catch (Exception e) {
            System.err.println("Token parsing error: " + e.getMessage());
            throw new RuntimeException("Invalid JWT token");
        }
    }

    public boolean validateToken(String token,String username) {
        String extractedUsername = extractUsername(token);
        try {
            Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .build()
                .parseClaimsJws(token);
            return extractedUsername.equals(username) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            System.err.println("Invalid JWT Token: " + e.getMessage());
            return false;
        }
    }
    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }
    
}