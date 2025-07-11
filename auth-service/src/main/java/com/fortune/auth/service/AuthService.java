package com.fortune.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fortune.auth.Response.Token;
import com.fortune.auth.entity.Customer;
import com.fortune.auth.enumeration.AuthMethod;
import com.fortune.auth.enumeration.Role;
import com.fortune.auth.repository.CustomerRepository;
import com.fortune.auth.request.CustomerSigninRequest;
import com.fortune.auth.request.CustomerSignupRequest;
import jakarta.validation.constraints.Email;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class AuthService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenService tokenService;

    public AuthService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService, TokenService tokenService) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.tokenService = tokenService;
    }

    public void signup(CustomerSignupRequest request) {
        Customer customer = Customer
                .builder()
                .authMethod(AuthMethod.EMAIL_PASSWORD)
                .email(request.email())
                .role(Role.CUSTOMER)
                .password(passwordEncoder.encode(request.password()))
                .username(request.username())
                .phoneNumber(request.phoneNumber())
                .build();
        customerRepository.save(customer);
    }

    public Token signIn(CustomerSigninRequest request){
        Authentication auth=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(),request.password()));
        if(auth.isAuthenticated()) {
           return new Token(jwtService.generateAccessToken(auth),
                   jwtService.generateRefreshToken(auth)
           );
        }

        throw  new AuthenticationCredentialsNotFoundException("Authentication Failed");
    }

    public Token refresh(String refreshToken)  {
        Authentication auth=authenticationManager.authenticate(new BearerTokenAuthenticationToken(refreshToken));
        if(auth.isAuthenticated()){
            return new Token(
                    jwtService.generateAccessTokenWithJWT(auth),
                    refreshToken
            );
        }

        throw  new AuthenticationCredentialsNotFoundException("Authentication Failed");
    }

    public void verifyEmail(String email,String token) {

    }
    public void sendOtp(String email) {
        tokenService.generateVerificationOtp(email);
    }
}
