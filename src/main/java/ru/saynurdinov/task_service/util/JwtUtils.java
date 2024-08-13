package ru.saynurdinov.task_service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${jwt.key}")
    private  String signingKey;

    @Value("${jwt.expiration}")
    private  Duration expiration;

    public String generateToken(UserDetails userDetails) {
        Date issuedDate = new Date();
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(issuedDate)
                .expiration(new Date(issuedDate.getTime() + expiration.toMillis()))
                .signWith(Keys.hmacShaKeyFor(signingKey.getBytes()))
                .compact();
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(signingKey.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
