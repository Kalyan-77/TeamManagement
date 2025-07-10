package com.example.TeamManager.Controller;

import com.example.TeamManager.Model.ERole;
import com.example.TeamManager.Model.Roles;
import com.example.TeamManager.Model.Users;
import com.example.TeamManager.Repository.RolesRepository;
import com.example.TeamManager.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;

    @Autowired
    public UserController(UserRepository userRepository, RolesRepository rolesRepository) {
        this.userRepository = userRepository;
        this.rolesRepository = rolesRepository;
    }

    @GetMapping
    public ResponseEntity<List<Users>> getAllUsers() {
        List<Users> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Users> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/roles")
    @PreAuthorize("hasRole('PROJECT_MANAGER')")
    public ResponseEntity<?> updateUserRoles(@PathVariable Long id,
                                             @RequestBody Map<String, Set<String>> rolesMap) {

        Set<String> roleNames = rolesMap.get("roles");

        return userRepository.findById(id).map(existingUser -> {

            Set<Roles> roles = roleNames.stream()
                    .map(roleName -> {
                        ERole eRole;
                        if (roleName.equalsIgnoreCase("manager")) {
                            eRole = ERole.ROLE_PROJECT_MANAGER;
                        } else if (roleName.equalsIgnoreCase("member")) {
                            eRole = ERole.ROLE_TEAM_MEMBER;
                        } else {
                            throw new RuntimeException("Unsupported role: " + roleName);
                        }

                        return rolesRepository.findByName(eRole)
                                .orElseThrow(() -> new RuntimeException("Role not found: " + eRole.name()));
                    })
                    .collect(Collectors.toSet());

            existingUser.setRoles(roles);
            userRepository.save(existingUser);

            return ResponseEntity.ok(existingUser);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROJECT_MANAGER')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
