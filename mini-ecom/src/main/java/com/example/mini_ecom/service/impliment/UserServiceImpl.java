package com.example.mini_ecom.service.impliment;

import java.time.Instant;
import java.util.NoSuchElementException;

import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.mini_ecom.model.User;
import com.example.mini_ecom.repository.CartRepository;
import com.example.mini_ecom.repository.OrderRepository;
import com.example.mini_ecom.repository.UserRepository;
import com.example.mini_ecom.service.UserService;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository; 
    private final CartRepository cartRepository; 

    public UserServiceImpl(UserRepository userRepository, OrderRepository orderRepository, CartRepository cartRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
    }
    
    @Override
    public User handleCreateUser(User newUser) {
        if (userRepository.existsByEmailAndDeletedAtIsNull(newUser.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        return this.userRepository.save(newUser);
    }

    @Override
    public User handleGetUserById(Long id) {
        return this.userRepository.findById(id).orElseThrow(() -> 
        new NoSuchElementException("User not found"));
    }

    @Override
    public User handleUpdateUser(Long id, User updateUser) {
        if (this.userRepository.existsByEmailAndDeletedAtIsNull(updateUser.getEmail())) {
            throw new DuplicateKeyException("Email already exists");
        }
        User currentUser = this.userRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> 
        new NoSuchElementException("User not found"));

        BeanUtils.copyProperties(updateUser, currentUser, "id","createdBy","createdAt","updatedBy","updatedAt");
        return this.userRepository.save(currentUser);
    }

    @Override
    @Transactional
    public void handleDeleteUser(Long id) {
        User currentUser = this.userRepository.findById(id).orElseThrow(() -> 
        new NoSuchElementException("User not found"));

        this.orderRepository.findByUserIdAndDeletedAtIsNull(id).forEach(order -> {
            order.setDeletedAt(Instant.now());
            this.orderRepository.save(order);
        });

        this.cartRepository.findByUserAndDeletedAtIsNull(currentUser).forEach(cart -> {
            cart.setDeletedAt(Instant.now());
            this.cartRepository.save(cart);
        });

        currentUser.setDeletedAt(Instant.now());
        this.userRepository.save(currentUser);
    }

    @Override
    public Page<User> handleGetAllUsers(Pageable userPageable) {
        return this.userRepository.findAll(userPageable);
    }
}
