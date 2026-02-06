package com.example.mini_ecom.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;

import com.example.mini_ecom.dto.auth.UserLoginDTO;


@Service
public class SecurityUtil {
    private final JwtEncoder jwtEncoder;

    @Value("${miniecom.jwt.base64-secret}")
    private String JWTkey;

    @Value("${miniecom.jwt.access-token-validity-in-seconds}")
    private long accessTokenExpriration;

    @Value("${miniecom.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpriration;

    public SecurityUtil(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public static MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS256;

    public SecretKey getSecretKey() {
        return new SecretKeySpec(JWTkey.getBytes(), JWT_ALGORITHM.getName());
    }

    public Jwt validRefreshToken(String refreshToken) {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(getSecretKey())
        .macAlgorithm(JWT_ALGORITHM).build();

        try {
            return jwtDecoder.decode(refreshToken);
        } catch (Exception e) {
            throw new RuntimeException("Invalid refresh token");
        }
    }

    public String createAccessToken(String email, UserLoginDTO userLoginDTO) {
        Instant now = Instant.now();
        Instant validity = now.plus(this.accessTokenExpriration, ChronoUnit.SECONDS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
        .issuer("self")
        .issuedAt(now)
        .expiresAt(validity)
        .subject(email)
        .claim("user", userLoginDTO)
        .build();

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader,claims)).getTokenValue();
    }

    public String createRefreshToken(String email) {
        Instant now = Instant.now();
        Instant validity = now.plus(this.refreshTokenExpriration, ChronoUnit.SECONDS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
        .issuer("self")
        .issuedAt(now)
        .expiresAt(validity)
        .subject(email)
        // .claim("user", userLoginDTO)
        .build();

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader,claims)).getTokenValue();
    }

    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));        
    }

    public static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        } else if (authentication.getPrincipal() instanceof String s) {
            return s;
        }
        return null;
    }
}
