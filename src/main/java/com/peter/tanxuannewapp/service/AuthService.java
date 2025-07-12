package com.peter.tanxuannewapp.service;

import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.peter.tanxuannewapp.domain.User;
import com.peter.tanxuannewapp.domain.Customer;
import com.peter.tanxuannewapp.repository.RoleRepository;
import com.peter.tanxuannewapp.repository.CustomerRepository;
import com.peter.tanxuannewapp.repository.UserRepository;
import com.peter.tanxuannewapp.domain.request.ReqLoginDTO;
import com.peter.tanxuannewapp.domain.resposne.ResLoginDTO;
import com.peter.tanxuannewapp.domain.resposne.ResUserDTO;
import com.peter.tanxuannewapp.domain.resposne.ResCustomerDTO;
import com.peter.tanxuannewapp.exception.ResourceAlreadyExistsException;
import com.peter.tanxuannewapp.exception.ResourceNotFoundException;
import com.peter.tanxuannewapp.util.JwtTokenUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CustomerService customerService;
    private final CustomerRepository customerRepository;

    public ResLoginDTO handleLoginForUserInternal(ReqLoginDTO reqLoginDTO) {
        // input username and password into Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                reqLoginDTO.getUsername(), reqLoginDTO.getPassword());

        // authenticate user => override loadUserByUserName()
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // set userLogin in SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO resLoginDTO = new ResLoginDTO();
        User currentUserDB = this.userService.handleGetUserByEmail(reqLoginDTO.getUsername());
        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currentUserDB.getId(), currentUserDB.getEmail(),
                    currentUserDB.getName(), currentUserDB.getRole());
            resLoginDTO.setUser(userLogin);
        }

        String accessToken = this.jwtTokenUtil.createAccessToken(authentication.getName(), resLoginDTO);
        resLoginDTO.setAccessToken(accessToken);

        return resLoginDTO;
    }

    public ResLoginDTO handleLoginForCustomer(ReqLoginDTO reqLoginDTO) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                reqLoginDTO.getUsername(), reqLoginDTO.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO resLoginDTO = new ResLoginDTO();
        Customer currentCustomerDB = this.customerService.handleGetCustomerByEmail(reqLoginDTO.getUsername());
        if (currentCustomerDB != null) {
            ResLoginDTO.UserLogin customerLogin = new ResLoginDTO.UserLogin(currentCustomerDB.getId(),
                    currentCustomerDB.getEmail(),
                    currentCustomerDB.getName(), currentCustomerDB.getRole());
            resLoginDTO.setUser(customerLogin);
        }

        String accessToken = this.jwtTokenUtil.createAccessToken(authentication.getName(), resLoginDTO);
        resLoginDTO.setAccessToken(accessToken);

        return resLoginDTO;
    }

    public String handleGetRefreshToken(String email, ResLoginDTO resLoginDTO) {
        String refreshToken = this.jwtTokenUtil.createRefreshToken(email, resLoginDTO);
        if (resLoginDTO.getUser().getRole().getName().equalsIgnoreCase("Customer")) {
            this.customerService.setRefreshTokenInCustomerDB(refreshToken, email);
        } else {
            this.userService.setRefreshTokenInUserDB(refreshToken, email);
        }
        return refreshToken;
    }

    public ResLoginDTO handleRenewRefreshToken(String refreshToken) {
        // check valid refreshToken
        Jwt decodeRefreshToken = this.jwtTokenUtil.checkValidRefreshToken(refreshToken);
        String email = decodeRefreshToken.getSubject();

        // try find User first
        ResLoginDTO resLoginDTO = new ResLoginDTO();

        // Check User first
        User currentUserDB = this.userService.handleGetUserByRefreshTokenAndEmail(refreshToken, email);
        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currentUserDB.getId(),
                    currentUserDB.getEmail(), currentUserDB.getName(), currentUserDB.getRole());
            resLoginDTO.setUser(userLogin);
        } else {
            // If User not found, try Customer
            Customer currentCustomerDB = this.customerService.handleGetCustomerByRefreshTokenAndEmail(refreshToken,
                    email);
            if (currentCustomerDB != null) {
                ResLoginDTO.UserLogin customerLogin = new ResLoginDTO.UserLogin(currentCustomerDB.getId(),
                        currentCustomerDB.getEmail(), currentCustomerDB.getName(), currentCustomerDB.getRole());
                resLoginDTO.setUser(customerLogin);
            } else {
                // Neither User nor Customer found
                throw new ResourceNotFoundException("Refresh token is invalid or expired!");
            }
        }

        String renewAccessToken = this.jwtTokenUtil.createAccessToken(email, resLoginDTO);
        resLoginDTO.setAccessToken(renewAccessToken);

        return resLoginDTO;
    }

    public void handleLogout() {
        String email = JwtTokenUtil
                .getCurrentUserLogin()
                .orElse("");
        if (email.isEmpty())
            throw new ResourceNotFoundException("Access token invalid!");

        // Try to logout User first, if not found try Customer
        User currentUser = this.userService.handleGetUserByEmail(email);
        if (currentUser != null) {
            this.userService.setRefreshTokenInUserDB(null, email);
        } else {
            Customer currentCustomer = this.customerService.handleGetCustomerByEmail(email);
            if (currentCustomer != null) {
                this.customerService.setRefreshTokenInCustomerDB(null, email);
            } else {
                throw new ResourceNotFoundException("User not found!");
            }
        }
    }

    public ResUserDTO handleRegisterForUserInternal(User userRegister) {
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

        return this.modelMapper.map(newUser, ResUserDTO.class);
    }

    public ResCustomerDTO handleRegisterForCustomer(Customer customerRegister) {
        if (this.customerService.checkEmailExists(customerRegister.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already exists!");
        }

        Customer newCustomer = new Customer();
        newCustomer.setEmail(customerRegister.getEmail());
        newCustomer.setPassword(this.passwordEncoder.encode(customerRegister.getPassword()));
        newCustomer.setName(customerRegister.getName());
        newCustomer.setAddress(customerRegister.getAddress());
        newCustomer.setPhone(customerRegister.getPhone());
        newCustomer.setRole(this.roleRepository.findByName("Customer"));

        this.customerRepository.save(newCustomer);

        return this.modelMapper.map(newCustomer, ResCustomerDTO.class);
    }

    public ResLoginDTO.UserAccount handleGetUserAccount() {
        String email = JwtTokenUtil.getCurrentUserLogin().orElse(null);
        if (email == null) {
            throw new ResourceNotFoundException("User not found!");
        }

        User currentUserDB = this.userService.handleGetUserByEmail(email);
        if (currentUserDB != null) {
            ResLoginDTO.UserAccount userAccount = new ResLoginDTO.UserAccount();
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                    currentUserDB.getId(),
                    currentUserDB.getEmail(),
                    currentUserDB.getName(),
                    currentUserDB.getRole());
            userAccount.setUser(userLogin);
            return userAccount;
        } else {
            Customer currentCustomerDB = this.customerService.handleGetCustomerByEmail(email);
            if (currentCustomerDB != null) {
                ResLoginDTO.UserAccount userAccount = new ResLoginDTO.UserAccount();
                ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                        currentCustomerDB.getId(),
                        currentCustomerDB.getEmail(),
                        currentCustomerDB.getName(),
                        currentCustomerDB.getRole());
                userAccount.setUser(userLogin);
                return userAccount;
            } else {
                throw new ResourceNotFoundException("User account not found!");
            }
        }
    }

}
