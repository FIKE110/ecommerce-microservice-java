package com.fortune.auth.controller;

import com.fortune.ApiResponse;
import com.fortune.ApiDataResponse;
import com.fortune.DataWrapper;
import com.fortune.auth.Response.MessageInString;
import com.fortune.auth.Response.Token;
import com.fortune.auth.enumeration.AuthResponseCode;
import com.fortune.auth.request.CustomerRefreshTokenRequest;
import com.fortune.auth.request.CustomerSigninRequest;
import com.fortune.auth.request.CustomerSignupRequest;
import com.fortune.auth.request.CustomerVerifyEmailRequest;
import com.fortune.auth.service.AuthService;
import com.fortune.auth.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.url}")
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;

    public AuthController(AuthService authService, TokenService tokenService) {
        this.authService = authService;
        this.tokenService = tokenService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<ApiDataResponse<DataWrapper<AuthResponseCode, MessageInString>>> signUp(@RequestBody @Valid  CustomerSignupRequest request){
        authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.data(
                                AuthResponseCode.SIGNUP_SUCCESSFUL,new MessageInString("Signup successful")))
                ;
    }

    @PostMapping("/sign-in")
    public ResponseEntity<ApiDataResponse<DataWrapper<AuthResponseCode,Token>>> signIn(@RequestBody @Valid CustomerSigninRequest request)  {
        Token token=authService.signIn(request);
        return ResponseEntity.ok().body(ApiResponse.data(
                        AuthResponseCode.SIGNIN_SUCCESSFUL,
                        token
        ));
    }

    @PostMapping("/send-otp")
    public ResponseEntity<ApiDataResponse<DataWrapper<AuthResponseCode,MessageInString>>> verifyEmail(@RequestBody @Valid CustomerVerifyEmailRequest request){
       tokenService.generateVerificationOtp(request.email());
       return ResponseEntity.ok(ApiResponse.data(
                       AuthResponseCode.VERIFICATION_OTP_SENT,
                       new MessageInString("Verification OTP sent")
       ));
    }

//    @PostMapping("/verify-email")
//    public ResponseEntity<ApiDataResponse<DataWrapper<AuthResponseCode,MessageInString>>> verifyEmail(@RequestBody @Valid CustomerVerifyEmailRequest request){
//        tokenService.ver(request.email());
//        return ResponseEntity.ok(new ApiDataResponse<>(
//                new DataWrapper<>(
//                        AuthResponseCode.VERIFICATION_OTP_SENT,
//                        new MessageInString("Verification OTP sent")
//                )
//        ));
//    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(){
        return ResponseEntity.ok().build();
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String,String>> profile(@RequestParam("username") String username){
        var profile=authService.getProfile(username);
        return ResponseEntity.ok(profile);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiDataResponse<DataWrapper<AuthResponseCode,Token>>> refreshToken(@RequestBody @Valid CustomerRefreshTokenRequest request) {
        Token token= authService.refresh(request.refreshToken());
        return ResponseEntity.ok(
               ApiResponse.data(
                                AuthResponseCode.TOKEN_REFRESHED,
                                token
                        )
        );
    }
}
