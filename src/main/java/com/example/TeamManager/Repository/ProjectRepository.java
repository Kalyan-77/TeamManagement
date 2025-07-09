package com.example.TeamManager.Repository;

import com.example.TeamManager.Model.Projects;
import com.example.TeamManager.Model.Users;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Projects,Long> {

    //1.Find by a project manager
    Optional<Projects> findByProjectManager(Users manager);

    //2.Find projects with pagination
    Page<Projects> findAll(Pageable pageable);

    //3.Find projects by name containing a String
    Page<Projects> findByNameContainingIgnoreCase(Strings name, Pageable pageable);

    //4. Count active projects (endDate is null or in the future)
    long countByEndDateIsNullOrEndDateAfter(LocalDate date);

    //5. 5. Find all projects ordered by creation date (most recent first)
    List<Projects> findAllByOrderByCreatedAtDesc();


}
