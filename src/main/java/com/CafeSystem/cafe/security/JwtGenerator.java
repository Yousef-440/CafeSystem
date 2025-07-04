package com.CafeSystem.cafe.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Component
public class JwtGenerator {
    @Value("${jwt.secret}")
    private String JWT_SECRETE;
    private static final long EXPIRATION_TIME = 60 * 60 * 1000;

    public String extractUsername(String token){
        return extractClaims(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
    }

    public Date extractExpiration(String token){
        return extractClaims(token, Claims::getExpiration);
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey getSignKey(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SECRETE));
    }

    public String generateToken(UserDetails userDetails, String role){
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return createToken(userDetails, claims);
    }


    private String createToken(UserDetails userDetails, Map<String,Object> claims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);
        return Jwts.builder()
                .setClaims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    private Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails){
        final String name = extractUsername(token);
        return (name.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
