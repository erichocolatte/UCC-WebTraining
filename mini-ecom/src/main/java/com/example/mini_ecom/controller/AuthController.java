package com.example.mini_ecom.controller;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mini_ecom.dto.ApiResponseDTO;
import com.example.mini_ecom.dto.auth.LoginDTO;
import com.example.mini_ecom.dto.auth.LoginResponseDTO;
import com.example.mini_ecom.dto.auth.RegisterDTO;
import com.example.mini_ecom.dto.auth.UserLoginDTO;
import com.example.mini_ecom.model.User;
import com.example.mini_ecom.service.UserService;
import com.example.mini_ecom.util.SecurityUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil, UserService userService, PasswordEncoder passwordEncoder) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Value("${miniecom.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpriration;


    @PostMapping("/auth/login")
    public  ResponseEntity<ApiResponseDTO<?>> login(
        @Valid @RequestBody LoginDTO loginDTO
    ) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        User currentUser = this.userService.handleGetUserByEmail(
            loginDTO.getEmail()
        );

        UserLoginDTO userLoginDTO = UserLoginDTO.builder()
            .id(currentUser.getId())
            .email(currentUser.getEmail())
            .name(currentUser.getName())
            .role(currentUser.getRole())
            .build();

        String accessToken = this.securityUtil.createAccessToken(currentUser.getEmail(),userLoginDTO);
        String refreshToken = this.securityUtil.createRefreshToken(currentUser.getEmail(), userLoginDTO);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        this.userService.handleUpdateRefreshToken(currentUser.getId(), refreshToken);

        ResponseCookie responseCookie = ResponseCookie
        .from("refreshToken", refreshToken)
        // .httpOnly(true)
        .secure(false)
        .path("/")
        .maxAge(refreshTokenExpriration)
        .build();

        return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
        .body(ApiResponseDTO.builder()
            .status(ApiResponseDTO.ResponseStatusDTO.builder()
                .statusCode(HttpStatus.OK)
                .message("Login success")
                .build()
            )
            .data(LoginResponseDTO.builder()
                .accessToken(accessToken)
                .user(currentUser)
                .build()
            )
            .timeStamp(Instant.now())
            .build()
        );   
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<ApiResponseDTO<?>> logout(
        @CookieValue(name = "refreshToken") String refreshToken
    ) {
        Jwt decodeToken = this.securityUtil.validRefreshToken(refreshToken);
        // from decode token get user id from claims
        // subject is email not id
        this.userService.handleUpdateRefreshToken(Long.parseLong(decodeToken.getClaimAsString("user")), null);
        ResponseCookie responseCookie = ResponseCookie
        .from("refreshToken", null)
        // .httpOnly(true)
        .secure(false)
        .path("/")
        .maxAge(0)
        .build();

        return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
        .body(ApiResponseDTO.builder()
            .status(ApiResponseDTO.ResponseStatusDTO.builder()
                .statusCode(HttpStatus.OK)
                .message("Logout success")
                .build()
            )
            .timeStamp(Instant.now())
            .build()
        );   
    }

    @GetMapping("/auth/account")
    public ResponseEntity<ApiResponseDTO<?>> account() {
        String userId = SecurityUtil.getCurrentUserLogin().get();
        User user = this.userService.handleGetUserById(Long.parseLong(userId));
        return ResponseEntity.ok()
        .body(ApiResponseDTO.builder()
            .status(ApiResponseDTO.ResponseStatusDTO.builder()
                .statusCode(HttpStatus.OK)
                .message("Get account success")
                .build()
            )
            .data(user)
            .timeStamp(Instant.now())
            .build()
        );   
    }

    @PostMapping("/auth/refresh-token")
    public ResponseEntity<ApiResponseDTO<?>> refreshToken(
        @CookieValue(name = "refreshToken",required = false) String refreshToken
    ) {
        if (refreshToken == null) {
            System.out.println("refreshToken is null");
        }
        System.out.println("cookie refreshToken = " + refreshToken);
        System.out.println("len = " + refreshToken.length());

        Jwt decodeToken = this.securityUtil.validRefreshToken(refreshToken);
        // String userId = decodeToken.getClaimAsString("userId");
        String userId = decodeToken.getClaim("userId").toString();
        User user = this.userService.handleGetUserById(Long.parseLong(userId));
        UserLoginDTO userLoginDTO = UserLoginDTO.builder()
            .id(user.getId())
            .email(user.getEmail())
            .name(user.getName())
            .role(user.getRole())
            .build();
        String newAccessToken = this.securityUtil.createAccessToken(user.getEmail(), userLoginDTO);
        String newRefreshToken = this.securityUtil.createRefreshToken(user.getEmail(), userLoginDTO);
        ResponseCookie responseCookie = ResponseCookie
        .from("refreshToken", newRefreshToken)
        // .httpOnly(false)
        .secure(false)
        .path("/")
        .maxAge(refreshTokenExpriration)
        .build();
        return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
        .body(ApiResponseDTO.builder()
            .status(ApiResponseDTO.ResponseStatusDTO.builder()
                .statusCode(HttpStatus.OK)
                .message("Refresh token success")
                .build()
            )
            .data(LoginResponseDTO.builder()
                .accessToken(newAccessToken)
                .user(user)
                .build()
            )
            .timeStamp(Instant.now())
            .build()
        );   
    }

    @PostMapping("/auth/register")
    public ResponseEntity<ApiResponseDTO<?>> register(
        @Valid @RequestBody RegisterDTO registerDTO
    ) {
        String hashPassword = this.passwordEncoder.encode(registerDTO.getPassword());

        User newUser = User.builder()
            .email(registerDTO.getEmail())
            .password(hashPassword)
            .name(registerDTO.getName())
            .role(registerDTO.getRole())
            .gender(registerDTO.getGender())
            .build();
        User createdUser = this.userService.handleCreateUser(newUser);

        return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponseDTO.builder()
            .status(ApiResponseDTO.ResponseStatusDTO.builder()
                .statusCode(HttpStatus.CREATED)
                .message("Register success")
                .build()
            )
            .data(createdUser)
            .timeStamp(Instant.now())
            .build()
        );   
    }
}
