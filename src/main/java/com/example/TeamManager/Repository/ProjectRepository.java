package com.example.TeamManager.Repository;

import com.example.TeamManager.Model.Projects;
import com.example.TeamManager.Model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Projects, Long> {

    // Find by project manager
    Optional<Projects> findByProjectManager(Users manager);

    // Find projects with pagination
    Page<Projects> findAll(Pageable pageable);

    // Find projects by name containing a keyword (case-insensitive)
    Page<Projects> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Count active projects (endDate is null or in the future)
    long countByEndDateIsNullOrEndDateAfter(LocalDate date);

    // Find all projects ordered by creation date (most recent first)
    List<Projects> findAllByOrderByCreatedAtDesc();
}
