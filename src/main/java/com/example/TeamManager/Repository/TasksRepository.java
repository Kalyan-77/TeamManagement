package com.example.TeamManager.Repository;

import com.example.TeamManager.Model.ETaskStatus;
import com.example.TeamManager.Model.Tasks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TasksRepository extends JpaRepository<Tasks, Long> {

    // 1. Find tasks by assigned user, with pagination
    Page<Tasks> findByAssignedToId(Long userId, Pageable pageable);

    // 2. Find tasks by project, with pagination
    Page<Tasks> findByProjectId(Long projectId, Pageable pageable);

    // 3. Find tasks by assigned user and status, with pagination
    Page<Tasks> findByAssignedToIdAndStatus(Long userId, ETaskStatus status, Pageable pageable);

    // 4. Find tasks by project and status, with pagination
    Page<Tasks> findByProjectIdAndStatus(Long projectId, ETaskStatus status, Pageable pageable);

    // 5. Find tasks by due date before or on a specific date
    List<Tasks> findByDueDateLessThanEqual(LocalDateTime dueDate);

    // 6. Get all tasks with pagination
    Page<Tasks> findAll(Pageable pageable);

    // 7. Find tasks by status (paginated)
    Page<Tasks> findByStatus(ETaskStatus status, Pageable pageable);

    // 8. Count tasks by status
    long countByStatus(ETaskStatus status);

    // 9. Count tasks due before or on a specific date
    long countByDueDateLessThanEqual(LocalDateTime dueDate);

    // 10. Find all tasks ordered by creation date (from recent tasks)
    List<Tasks> findAllByOrderByCreatedAtDesc();
}
