package com.example.TeamManager.Repository;

import com.example.TeamManager.Model.ERole;
import com.example.TeamManager.Model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolesRepository extends JpaRepository<Roles,Long> {
    Optional<Roles> findByName(ERole name);
}
