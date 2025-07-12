package com.peter.tanxuannewapp.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.peter.tanxuannewapp.domain.Customer;
import com.peter.tanxuannewapp.domain.User;
import com.peter.tanxuannewapp.domain.annotation.ApiMessage;
import com.peter.tanxuannewapp.domain.request.ReqLoginDTO;
import com.peter.tanxuannewapp.domain.resposne.ResCustomerDTO;
import com.peter.tanxuannewapp.domain.resposne.ResLoginDTO;
import com.peter.tanxuannewapp.domain.resposne.ResUserDTO;
import com.peter.tanxuannewapp.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Transactional
public class AuthController {
        private final AuthService authService;
        private static final String REFRESH_TOKEN = "refresh_token";

        @Value("${peterBui.jwt.refresh-token-validity-in-seconds}")
        private long refreshTokenValidityInSeconds;

        @Value("${peterBui.path.cookie}")
        private String pathCookie;

        @PostMapping("/admin/login")
        @ApiMessage("Login for user internal")
        public ResponseEntity<ResLoginDTO> loginForUserInternal(@RequestBody @Valid ReqLoginDTO reqLoginDTO) {
                ResLoginDTO resLoginDTO = this.authService.handleLoginForUserInternal(reqLoginDTO);

                // get refreshToken and set in DB
                String refreshToken = this.authService.handleGetRefreshToken(resLoginDTO.getUser().getEmail(),
                                resLoginDTO);

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

        @PostMapping("/login")
        @ApiMessage("Login for customer")
        public ResponseEntity<ResLoginDTO> loginForCustomer(@RequestBody @Valid ReqLoginDTO reqLoginDTO) {
                ResLoginDTO resLoginDTO = this.authService.handleLoginForCustomer(reqLoginDTO);

                // get refreshToken and set in DB
                String refreshToken = this.authService.handleGetRefreshToken(resLoginDTO.getUser().getEmail(),
                                resLoginDTO);

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
        public ResponseEntity<ResLoginDTO> renewRefreshToken(
                        @CookieValue(value = REFRESH_TOKEN, defaultValue = "false") String refreshToken) {
                ResLoginDTO resLoginDTO = this.authService.handleRenewRefreshToken(refreshToken);
                String renewRefreshToken = this.authService.handleGetRefreshToken(resLoginDTO.getUser().getEmail(),
                                resLoginDTO);

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
                this.authService.handleLogout();

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

        @PostMapping("/admin/register")
        @ApiMessage("Register for user internal")
        public ResponseEntity<ResUserDTO> registerForUserInternal(@RequestBody @Valid User userRegister) {
                ResUserDTO resUserDTO = this.authService.handleRegisterForUserInternal(userRegister);
                return ResponseEntity
                                .status(HttpStatus.CREATED.value())
                                .body(resUserDTO);
        }

        @PostMapping("/register")
        @ApiMessage("Register for customer")
        public ResponseEntity<ResCustomerDTO> registerForCustomer(@RequestBody @Valid Customer customerRegister) {
                ResCustomerDTO resCustomerDTO = this.authService.handleRegisterForCustomer(customerRegister);
                return ResponseEntity
                                .status(HttpStatus.CREATED.value())
                                .body(resCustomerDTO);
        }

        @GetMapping("/account")
        @ApiMessage("Get user account")
        public ResponseEntity<ResLoginDTO.UserAccount> getUserAccount() {
                ResLoginDTO.UserAccount userAccount = this.authService.handleGetUserAccount();

                return ResponseEntity
                                .ok()
                                .body(userAccount);
        }

}
