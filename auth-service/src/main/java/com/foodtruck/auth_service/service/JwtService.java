package com.foodtruck.auth_service.service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    // Lee el secreto desde application.properties
    // Si no está definido, usa este valor por defecto
    @Value("${jwt.secret:esta_es_mi_compleja_clave_secreta_foodtruck_2026}")
    private String secret;

    private Key getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Genera un token JWT con el email del usuario, dura 1 hora
    public String generateToken(String email) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + 1000L * 60 * 60);

        log.info("Generando token JWT para: {}", email);
        return Jwts.builder()
                .subject(email)
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(getKey())
                .compact();
    }

    // Extrae el email desde un token (quita el "Bearer " si viene así)
    public String getEmailFromToken(String token) {
        if (token == null || token.isBlank()) return null;
        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        try {
            return Jwts.parser()
                    .verifyWith((SecretKey) getKey())
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload()
                    .getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Token inválido: {}", e.getMessage());
            return null;
        }
    }

    // Valida si un token es válido (firma correcta y no expirado)
    public boolean isValid(String token) {
        if (token == null || token.isBlank()) return false;
        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) getKey())
                    .build()
                    .parseSignedClaims(jwt);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Token inválido al validar: {}", e.getMessage());
            return false;
        }
    }
}
