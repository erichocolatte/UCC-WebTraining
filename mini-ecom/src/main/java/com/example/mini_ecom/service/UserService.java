package com.example.mini_ecom.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.mini_ecom.model.User;

public interface UserService {
    // Create
    public User handleCreateUser(User user);

    public User handleGetUserById(Long id);

    public User handleUpdateUser(Long id, User user);

    public void handleDeleteUser(Long id);

    public Page<User> handleGetAllUsers(Pageable userPageable);
}
