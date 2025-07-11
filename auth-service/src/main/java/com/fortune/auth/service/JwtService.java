package com.fortune.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fortune.auth.Response.Token;
import com.fortune.auth.entity.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    public JwtService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    public String generateAccessTokenWithJWT(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String scope=authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        Instant now = Instant.now();
        JwtClaimsSet claims=JwtClaimsSet.builder()
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .issuer("self")
                .issuedAt(now)
                .claim("scope",jwt.getClaim("scope"))
                .claim("purpose","access")
                .subject(jwt.getSubject())
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
    public String generateAccessToken(Authentication authentication) {
        String scope=authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        Customer customer = (Customer) authentication.getPrincipal();
        Instant now = Instant.now();
        JwtClaimsSet claims=JwtClaimsSet.builder()
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .issuer("self")
                .issuedAt(now)
                .claim("scope",scope)
                .claim("purpose","access")
                .subject(customer.getUsername())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public Jwt validateRefreshToken(String token) {
        var jwt=jwtDecoder.decode(token);
        String purpose=jwt.getClaim("purpose");
        if (!purpose.equals("refresh")) throw new RuntimeException("invalid token");
        assert jwt.getIssuedAt() != null;
        if(!Instant.now().isBefore(jwt.getIssuedAt()) && jwt.getSubject()!=null) throw new RuntimeException("expired token");;
        return jwt;
    }

    public String generateRefreshToken(Authentication authentication) {
        String scope=authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        var customer = (Customer) authentication.getPrincipal();
        Instant now = Instant.now();
        JwtClaimsSet claims=JwtClaimsSet.builder()
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .issuer("refresh")
                .issuedAt(now)
                .claim("scope",scope)
                .subject(customer.getUsername())
                .claim("purpose", "refresh")
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}