package com.example.mini_ecom.config;

import java.util.Collections;
import java.util.NoSuchElementException;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    public CustomUserDetails loadUserByUsername(String username) {
        User currentUser = this.userRepository.findByEmailAndDeletedAtIsNull(username).orElseThrow(() -> 
        new UsernameNotFoundException("User not found"));

        return new CustomUserDetails(
            currentUser.getId(),
            currentUser.getEmail(),
            currentUser.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + currentUser.getRole().name()))
        );
    }
}
