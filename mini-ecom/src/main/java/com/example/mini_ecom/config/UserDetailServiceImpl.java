package com.example.mini_ecom.config;

import java.util.Collections;
import java.util.NoSuchElementException;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.example.mini_ecom.model.User;
import com.example.mini_ecom.repository.UserRepository;

@Component("userDetailService")
public class UserDetailServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User currentUser = this.userRepository.findByEmailAndDeletedAtIsNull(username).orElseThrow(() -> 
        new NoSuchElementException("User not found"));

        return new org.springframework.security.core.userdetails.User(
            currentUser.getEmail(),
            currentUser.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority(currentUser.getRole().name()))
        );
    }
}
