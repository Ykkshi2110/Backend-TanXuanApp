package com.peter.tanxuannewapp.util;

import com.nimbusds.jose.util.Base64;
import com.peter.tanxuannewapp.domain.resposne.ResLoginDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {
    @Value("${peterBui.jwt.base64-secret}")
    private String jwtSecretKey;

    @Value("${peterBui.jwt.access-token-validity-in-seconds}")
    private long accessTokenExpiration;

    @Value("${peterBui.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    private final JwtEncoder jwtEncoder;
    private final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

    public String createAccessToken(String email, ResLoginDTO resLoginDTO) {
        // store basic information user in token
        ResLoginDTO.UserInsideToken userInsideToken = new ResLoginDTO.UserInsideToken();
        userInsideToken.setId(resLoginDTO
                .getUser()
                .getId());
        userInsideToken.setEmail(resLoginDTO
                .getUser()
                .getEmail());
        userInsideToken.setName(resLoginDTO
                .getUser()
                .getName());

        Instant now = Instant.now();
        Instant validity = now.plus(this.accessTokenExpiration, ChronoUnit.SECONDS); // cast long -> instant

        // get Role in authentication and set Role in claim
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
        String authority = authentication
                .getAuthorities().toString();
        JwtClaimsSet claimsSet = JwtClaimsSet
                .builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(email)
                .claim("user", userInsideToken)
                .claim("authority", authority)
                .build();
        JwsHeader jwsHeader = JwsHeader
                .with(JWT_ALGORITHM)
                .build();
        return this.jwtEncoder
                .encode(JwtEncoderParameters.from(jwsHeader, claimsSet))
                .getTokenValue();
    }

    public String createRefreshToken(String email, ResLoginDTO resLoginDTO) {
        ResLoginDTO.UserInsideToken userInsideToken = new ResLoginDTO.UserInsideToken();
        userInsideToken.setId(resLoginDTO
                .getUser()
                .getId());
        userInsideToken.setEmail(resLoginDTO
                .getUser()
                .getEmail());
        userInsideToken.setName(resLoginDTO
                .getUser()
                .getName());

        Instant now = Instant.now();
        Instant validity = now.plus(this.refreshTokenExpiration, ChronoUnit.SECONDS);

        JwtClaimsSet claimsSet = JwtClaimsSet
                .builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(email)
                .claim("user", userInsideToken)
                .build();
        JwsHeader jwsHeader = JwsHeader
                .with(JWT_ALGORITHM)
                .build();
        return this.jwtEncoder
                .encode(JwtEncoderParameters.from(jwsHeader, claimsSet))
                .getTokenValue();
    }

    private SecretKey getJwtSecretKey() {
        byte[] keyBytes = Base64
                .from(jwtSecretKey)
                .decode();
        return new SecretKeySpec(keyBytes, JWT_ALGORITHM.getName());
    }

    public Jwt checkValidRefreshToken(String refreshToken)  {

        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder
                .withSecretKey(getJwtSecretKey())
                .macAlgorithm(JWT_ALGORITHM)
                .build();
        try {
            return jwtDecoder.decode(refreshToken);
        } catch (BadJwtException e) {
            logger.error(">>> Refresh Token error: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Get the login of the current user.
     *
     * @return the login of the current user.
     */
    public static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails springUserDetail) {
            return springUserDetail.getUsername();
        } else if (authentication.getPrincipal() instanceof String stringPrincipal) {
            return stringPrincipal;
        } else if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        }
        return null;
    }

    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

}
