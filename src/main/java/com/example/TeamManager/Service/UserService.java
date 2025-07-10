package com.example.TeamManager.Service;

import com.example.TeamManager.Model.Roles;
import com.example.TeamManager.Model.Users;
import com.example.TeamManager.Repository.ProjectRepository;
import com.example.TeamManager.Repository.RolesRepository;
import com.example.TeamManager.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       ProjectRepository projectRepository,
                       RolesRepository rolesRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.rolesRepository = rolesRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register a new user with validation, password encoding, and role assignment
     */
    public Users registerUser(String username, String email, String password, Set<String> strRoles) {
        // Check Username Availability
        if (userRepository.existsByName(username)) {
            throw new IllegalArgumentException("Error: Username is already taken!");
        }

        // Check Email Availability
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Error: Email is already in use!");
        }

        // Create New User Object
        Users user = new Users();
        user.setName(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        // Assign Roles
        Set<Roles> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            // Default to TEAM_MEMBER
            Roles memberRole = rolesRepository.findByName("ROLE_TEAM_MEMBER")
                    .orElseThrow(() -> new RuntimeException("Error: Role 'TEAM_MEMBER' not found."));
            roles.add(memberRole);
        } else {
            for (String role : strRoles) {
                switch (role.toLowerCase()) {
                    case "manager":
                        Roles managerRole = rolesRepository.findByName("ROLE_PROJECT_MANAGER")
                                .orElseThrow(() -> new RuntimeException("Error: Role 'PROJECT_MANAGER' not found."));
                        roles.add(managerRole);
                        break;
                    case "member":
                        Roles memberRole = rolesRepository.findByName("ROLE_TEAM_MEMBER")
                                .orElseThrow(() -> new RuntimeException("Error: Role 'TEAM_MEMBER' not found."));
                        roles.add(memberRole);
                        break;
                    default:
                        throw new RuntimeException("Error: Role '" + role + "' is not supported.");
                }
            }
        }

        // Set Roles and Save User
        user.setRoles(roles);
        return userRepository.save(user);
    }

    /**
     * Find user by ID
     */
    public Optional<Users> findUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Find user by username
     */
    public Optional<Users> findByUserName(String username) {
        return userRepository.findByName(username);
    }

    /**
     * Retrieve all users
     */
    public List<Users> findAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Delete a user by ID
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Update a user's roles
     */
    public Users updateUserRoles(Long userId, Set<String> newRoles) {
        // Find user
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // Prepare new roles
        Set<Roles> roles = new HashSet<>();

        for (String roleName : newRoles) {
            String roleKey;
            switch (roleName.toLowerCase()) {
                case "manager":
                    roleKey = "ROLE_PROJECT_MANAGER";
                    break;
                case "member":
                    roleKey = "ROLE_TEAM_MEMBER";
                    break;
                default:
                    throw new IllegalArgumentException("Role '" + roleName + "' is not supported.");
            }

            Roles role = rolesRepository.findByName(roleKey)
                    .orElseThrow(() -> new RuntimeException("Error: Role '" + roleName + "' not found."));
            roles.add(role);
        }

        // Update and save
        user.setRoles(roles);
        return userRepository.save(user);
    }
}
