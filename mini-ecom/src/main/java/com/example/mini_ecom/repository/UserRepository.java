package com.example.mini_ecom.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mini_ecom.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    public Optional<User> findByIdAndDeletedAtIsNull(Long id);
    public Optional<User> findByEmailAndDeletedAtIsNull(String email);
    public Boolean existsByEmailAndDeletedAtIsNull(String email);
    public Boolean existsByNameAndDeletedAtIsNull(String name);

    // public Optional<User> findByEmailAndDeletedAtIsNull(String email);
}
