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
        return Jwts.parserBuilder()
            .setSigningKey(jwtSecretKey) // New API: Use parserBuilder() instead of parser()
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
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