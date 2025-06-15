package com.peter.tanxuannewapp.controller;

import com.peter.tanxuannewapp.domain.User;
import com.peter.tanxuannewapp.domain.annotation.ApiMessage;
import com.peter.tanxuannewapp.domain.request.ReqLoginDTO;
import com.peter.tanxuannewapp.domain.resposne.ResLoginDTO;
import com.peter.tanxuannewapp.domain.resposne.ResUserDTO;
import com.peter.tanxuannewapp.exception.ResourceAlreadyExistsException;
import com.peter.tanxuannewapp.exception.ResourceNotFoundException;
import com.peter.tanxuannewapp.repository.RoleRepository;
import com.peter.tanxuannewapp.repository.UserRepository;
import com.peter.tanxuannewapp.service.UserService;
import com.peter.tanxuannewapp.util.JwtTokenUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Transactional
public class AuthController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private static final String REFRESH_TOKEN = "refresh_token";
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Value("${peterBui.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenValidityInSeconds;

    @Value("${peterBui.path.cookie}")
    private String pathCookie;

    @PostMapping("/login")
    @ApiMessage("Login for user in company")
    public ResponseEntity<ResLoginDTO> loginForUserInCompany(@RequestBody @Valid ReqLoginDTO reqLoginDTO) {
        // input username and password into Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(reqLoginDTO.getUsername(), reqLoginDTO.getPassword());

        // authenticate user => override loadUserByUserName()
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // set userLogin in SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO resLoginDTO = new ResLoginDTO();
        User currentUserDB = this.userService.handleGetUserByEmail(reqLoginDTO.getUsername());
        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currentUserDB.getId(), currentUserDB.getEmail(), currentUserDB.getName(), currentUserDB.getRole());
            resLoginDTO.setUser(userLogin);
        }

        String accessToken = this.jwtTokenUtil.createAccessToken(authentication.getName(), resLoginDTO);
        resLoginDTO.setAccessToken(accessToken);

        String refreshToken = this.jwtTokenUtil.createRefreshToken(authentication.getName(), resLoginDTO);
        // get refreshToken in DB
        this.userService.setRefreshTokenInUserDB(refreshToken, reqLoginDTO.getUsername());

        // store refreshToken in Cookie
        ResponseCookie springCookie = ResponseCookie
                .from(REFRESH_TOKEN, refreshToken)
                .secure(true)
                .httpOnly(true)
                .maxAge(refreshTokenValidityInSeconds)
                .path(pathCookie)
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, springCookie.toString())
                .body(resLoginDTO);
    }

    @GetMapping("/refresh")
    @ApiMessage("Refresh Token")
    public ResponseEntity<ResLoginDTO> renewRefreshToken(@CookieValue(value = REFRESH_TOKEN, defaultValue = "false") String refreshToken)  {
        // check valid refreshToken
        Jwt decodeRefreshToken = this.jwtTokenUtil.checkValidRefreshToken(refreshToken);
        String email = decodeRefreshToken.getSubject();

        // check refreshToken and email exists in DB
        User currentUserDB = this.userService.handleGetUserByRefreshTokenAndEmail(refreshToken, email);
        ResLoginDTO resLoginDTO = new ResLoginDTO();
        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currentUserDB.getId(), currentUserDB.getEmail(), currentUserDB.getName(), currentUserDB.getRole());
            resLoginDTO.setUser(userLogin);
        }

        String renewAccessToken = this.jwtTokenUtil.createAccessToken(email, resLoginDTO);
        resLoginDTO.setAccessToken(renewAccessToken);

        String renewRefreshToken = this.jwtTokenUtil.createRefreshToken(email, resLoginDTO);
        this.userService.setRefreshTokenInUserDB(renewRefreshToken, email);

        // store in Cookie, oldCookie => newCookie
        ResponseCookie springCookie = ResponseCookie
                .from(REFRESH_TOKEN, renewRefreshToken)
                .secure(true)
                .httpOnly(true)
                .maxAge(refreshTokenValidityInSeconds)
                .path(pathCookie)
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, springCookie.toString())
                .body(resLoginDTO);
    }

    @PostMapping("/logout")
    @ApiMessage("Logout user")
    public ResponseEntity<Void> logout() {
        String email = JwtTokenUtil
                .getCurrentUserLogin()
                .orElse("");
        if (email.isEmpty()) throw new ResourceNotFoundException("Access token invalid!");

        // set null for refreshToken
        this.userService.setRefreshTokenInUserDB(null, email);

        // revoke refreshToken in server
        ResponseCookie revokeCookie = ResponseCookie
                .from(REFRESH_TOKEN, null)
                .httpOnly(true)
                .secure(true)
                .path(pathCookie)
                .maxAge(0)
                .build();
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, revokeCookie.toString())
                .body(null);
    }

    @PostMapping("/register")
    @ApiMessage("Register")
    public ResponseEntity<ResUserDTO> register(@RequestBody @Valid User userRegister) {
        if (this.userService.checkEmailExists(userRegister.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already exists!");
        }

        User newUser = new User();
        newUser.setEmail(userRegister.getEmail());
        newUser.setPassword(this.passwordEncoder.encode(userRegister.getPassword()));
        newUser.setName(userRegister.getName());
        newUser.setAddress(userRegister.getAddress());
        newUser.setPhone(userRegister.getPhone());
        newUser.setRole(this.roleRepository.findByName("User"));
        this.userRepository.save(newUser);

        ResUserDTO userDTO = this.modelMapper.map(newUser, ResUserDTO.class);
        return ResponseEntity
                .status(HttpStatus.CREATED.value())
                .body(userDTO);
    }

    @GetMapping("/account")
    @ApiMessage("Get user account")
    public ResponseEntity<ResLoginDTO.UserAccount> getUserAccount() {

        User currentUserDB = this.userService.handleGetUserByEmail(JwtTokenUtil.getCurrentUserLogin().orElse(null));
        ResLoginDTO.UserAccount userAccount = new ResLoginDTO.UserAccount();
        if (currentUserDB != null) {
            userAccount = this.modelMapper.map(currentUserDB, ResLoginDTO.UserAccount.class);
        }

        return ResponseEntity
                .ok()
                .body(userAccount);
    }

}
