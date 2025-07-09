package com.example.TeamManager.Service;

import com.example.TeamManager.Model.Roles;
import com.example.TeamManager.Model.Users;
import com.example.TeamManager.Repository.ProjectRepository;
import com.example.TeamManager.Repository.RolesRepository;
import com.example.TeamManager.Repository.UserRepository;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    //create a new user object
    public Users createUser(Users users){
        return userRepository.save(users);
    }

    //initialize a new user object
    //set the username of the new user to the provided username
    //set the email of the new user to the provided password
    //hash the provided password  using passwordEncoder.encode() and set it as the users password
    public Users registerUser(String username, String email, String password, String role) {
        Users newUser = new Users();
        newUser.setName(username);
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password));

        Set<Roles> roles = new HashSet<>();

        if ("manager".equalsIgnoreCase(role)) {
            Roles managerRole = rolesRepository.findByName("ROLE_PROJECT_MANAGER")
                    .orElseThrow(() -> new RuntimeException("Error: Role 'PROJECT MANAGER' not found."));
            roles.add(managerRole);
        } else if ("member".equalsIgnoreCase(role)) {
            Roles memberRole = rolesRepository.findByName("ROLE_TEAM_MEMBER")
                    .orElseThrow(() -> new RuntimeException("Error: Role 'TEAM MEMBER' not found."));
            roles.add(memberRole);
        } else {
            throw new RuntimeException("Error: Invalid role specified. Must be either 'manager' or 'member'.");
        }

        newUser.setRoles(roles);
        return userRepository.save(newUser);
    }

    public Optional<Users> findUserById(Long id){
        return userRepository.findById(id);
    }

    public Optional<Users> findByUserName(String username){
        return userRepository.findByName(username);
    }

    public List<Users> findAllUsers(){
        return userRepository.findAll();
    }

    void deleteUser(Long id){
        try{
            userRepository.deleteById(id);
        }catch (IllegalArgumentException e){
            System.err.println("User not found with ID: " + id);
        }
    }

    public Users updateUserRoles(Long id){
        try{
            Users user = userRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with id " + id));
            Roles role = rolesRepository.findByName("MANAGER")
                    .orElseThrow(() -> new RuntimeException("The role 'PROJECT_MANAGER' is not found in roleRepository"));

            Set<Roles> roles = new HashSet<>();
            roles.add(role);
            user.setRoles(roles);

            return userRepository.save(user);
        }catch (IllegalArgumentException e){
            System.err.println("User not found with id " + id);
        }catch (RuntimeException e){
            System.err.println("The role is not found in roleRepository with id : " + id);
        }
    }

}
