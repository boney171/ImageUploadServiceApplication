package com.example.demo.auth.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class JWTUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.issuer}")
    private String issuer;

    private final long validityInMillis = 15 * 60 * 1000;

    public String generateToken(String username, long id) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInMillis);

        return JWT.create()
                .withIssuer(issuer)
                .withSubject(username)
                .withClaim("user_id", id)
                .withIssuedAt(now)
                .withExpiresAt(expiry)
                .sign(Algorithm.HMAC256(secret));
    }

    public String validateTokenAndGetUsername(String token) {
        try {
            var verifier = JWT.require(Algorithm.HMAC256(secret))
                    .withIssuer(issuer)
                    .build();

            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getSubject();
        } catch (JWTVerificationException ex) {
            return null;
        }
    }

    public Long getUserIdFromToken(String token) {
        try {
            var verifier = JWT.require(Algorithm.HMAC256(secret))
                    .withIssuer(issuer)
                    .build();

            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getClaim("user_id").asLong();
        } catch (JWTVerificationException ex) {
            return null;
        }
    }
}