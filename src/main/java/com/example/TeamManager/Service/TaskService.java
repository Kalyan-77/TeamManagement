// --- TaskService.java ---
package com.example.TeamManager.Service;

import com.example.TeamManager.DTO.TaskDTO;
import com.example.TeamManager.Model.ETaskPriority;
import com.example.TeamManager.Model.ETaskStatus;
import com.example.TeamManager.Model.Projects;
import com.example.TeamManager.Model.Tasks;
import com.example.TeamManager.Model.Users;
import com.example.TeamManager.Repository.ProjectRepository;
import com.example.TeamManager.Repository.TasksRepository;
import com.example.TeamManager.Repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TaskService {
    private final TasksRepository tasksRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public TaskService(TasksRepository tasksRepository, ProjectRepository projectRepository, UserRepository userRepository) {
        this.tasksRepository = tasksRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public Tasks createTaskFromRequest(TaskDTO request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }

        Tasks task = new Tasks();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus() != null ? request.getStatus() : ETaskStatus.TO_DO);
        task.setPriority(request.getPriority() != null ? request.getPriority() : ETaskPriority.MEDIUM);
        task.setDueDate(request.getDueDate());

        if (request.getProjectId() == null || !projectRepository.existsById(request.getProjectId())) {
            throw new IllegalArgumentException("Project not found with ID: " + request.getProjectId());
        }
        task.setProject(projectRepository.findById(request.getProjectId()).get());

        if (request.getAssignedToId() != null) {
            Users assignedUser = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new IllegalArgumentException("Assigned user not found with ID: " + request.getAssignedToId()));
            task.setAssignedTo(assignedUser);
        }

        return tasksRepository.save(task);
    }

    public Optional<Tasks> getTaskById(Long id) {
        return tasksRepository.findById(id);
    }

    public Page<Tasks> getAllTasks(Pageable pageable) {
        return tasksRepository.findAll(pageable);
    }

    public Page<Tasks> getTasksByAssignedUser(Long id, Pageable pageable) {
        return tasksRepository.findByAssignedToId(id, pageable);
    }

    public Page<Tasks> getTasksByAssignedUserAndStatus(Long userId, ETaskStatus status, Pageable pageable) {
        return tasksRepository.findByAssignedToIdAndStatus(userId, status, pageable);
    }

    public Page<Tasks> getTasksByProjectAndStatus(Long projectId, ETaskStatus status, Pageable pageable) {
        return tasksRepository.findByProjectIdAndStatus(projectId, status, pageable);
    }

    public Page<Tasks> getTasksByStatus(ETaskStatus status, Pageable pageable) {
        return tasksRepository.findByStatus(status, pageable);
    }

    public Page<Tasks> getTasksByProject(Long projectId, Pageable pageable) {
        return tasksRepository.findByProjectId(projectId, pageable);
    }

    public void deleteTask(Long id) {
        if (!tasksRepository.existsById(id)) {
            throw new IllegalArgumentException("Task not found with id: " + id);
        }
        tasksRepository.deleteById(id);
    }

    public Tasks updateTask(Long id, Tasks updatedTask) {
        Tasks existingTask = tasksRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + id));

        if (updatedTask.getTitle() != null && !updatedTask.getTitle().trim().isEmpty()) {
            existingTask.setTitle(updatedTask.getTitle());
        }

        if (updatedTask.getDescription() != null && !updatedTask.getDescription().trim().isEmpty()) {
            existingTask.setDescription(updatedTask.getDescription());
        }

        if (updatedTask.getStatus() != null) {
            existingTask.setStatus(updatedTask.getStatus());
        }

        if (updatedTask.getPriority() != null) {
            existingTask.setPriority(updatedTask.getPriority());
        }

        if (updatedTask.getDueDate() != null) {
            existingTask.setDueDate(updatedTask.getDueDate());
        }

        if (existingTask.getDueDate() != null && existingTask.getDueDate().isBefore(LocalDateTime.now()) && existingTask.getStatus() != ETaskStatus.DONE) {
            System.out.println("Warning: Task is overdue and not completed.");
        }

        if (updatedTask.getAssignedTo() != null) {
            if (updatedTask.getAssignedTo().getId() != null) {
                Long newAssignedId = updatedTask.getAssignedTo().getId();
                Users newAssignee = userRepository.findById(newAssignedId)
                        .orElseThrow(() -> new IllegalArgumentException("New Assigned user not found with id: " + newAssignedId));
                existingTask.setAssignedTo(newAssignee);
            } else {
                existingTask.setAssignedTo(null);
            }
        }

        return tasksRepository.save(existingTask);
    }

    public Tasks updateTaskStatus(Long taskId, ETaskStatus status) {
        Tasks tasks = tasksRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + taskId));
        tasks.setStatus(status);
        return tasksRepository.save(tasks);
    }
}
