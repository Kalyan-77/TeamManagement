package com.example.TeamManager.Repository;

import com.example.TeamManager.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    // 1. Find user by username
    Optional<Users> findByName(String name);

    // 2. Check if a user with a given username exists
    boolean existsByName(String name);

    // 3. Check if a user with a given email exists
    boolean existsByEmail(String email);
}
