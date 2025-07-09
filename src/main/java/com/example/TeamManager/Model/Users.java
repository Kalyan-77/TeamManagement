package com.example.TeamManager.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.management.relation.Role;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
//@RequiredArgsConstructor
public class Users {
    @Id//makes it primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotBlank(message = "Name is Required")
    @Size(min = 2,max = 100, message = "Name must be between 2 to 100 characters")
    private String name;

    @NotNull
    @NotBlank(message = "Email is Required")
    @Email(message = "Email should be valid")
    @Column(unique = true)
    private String email;

    @NotNull
    @NotBlank(message = "Password is required")
    private String password;

    @Column(name = "created_at",updatable = false)
    @CreationTimestamp
    private LocalDateTime updatedTime;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Roles> roles = new HashSet<>();

    @OneToMany(mappedBy = "assignedTo")
    private List<Tasks> assignedTasks;

    @OneToMany(mappedBy = "projectManager")
    private List<Projects> managedProjects;

}
