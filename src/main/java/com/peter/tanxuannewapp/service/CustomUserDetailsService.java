package com.peter.tanxuannewapp.service;

import com.peter.tanxuannewapp.domain.User;
import com.peter.tanxuannewapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;

@RequiredArgsConstructor
@Component("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findUserByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Username/password is incorrect!"));
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getName())));
    }
}
