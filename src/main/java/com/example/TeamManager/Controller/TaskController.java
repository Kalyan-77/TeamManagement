package com.example.TeamManager.Controller;

import com.example.TeamManager.DTO.TaskDTO;
import com.example.TeamManager.Model.ETaskStatus;
import com.example.TeamManager.Model.Tasks;
import com.example.TeamManager.Repository.ProjectRepository;
import com.example.TeamManager.Repository.TasksRepository;
import com.example.TeamManager.Service.TaskService;
import com.example.TeamManager.Service.UserService;
import jakarta.validation.Valid;
import org.hibernate.query.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;
    private final UserService userService;

    @Autowired
    public TaskController(TaskService taskService, UserService userService){
        this.taskService = taskService;
        this.userService = userService;
    }

    //Create Task
    @PostMapping
    public ResponseEntity<Tasks> createtask(@RequestBody Tasks task){
        Tasks createdTask = taskService.createTask(task);
        return ResponseEntity.ok(createdTask);
    }

    //get All Task
    @GetMapping
    public ResponseEntity<Page<Tasks>> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Tasks> tasks = taskService.getAllTasks(PageRequest.of(page, size));
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(task -> ResponseEntity.ok().body(task))
                .orElse(ResponseEntity.notFound().build());
    }


    //Get Tasks by Assigned User
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<Tasks>> getTasksByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Tasks> tasks = taskService.getTasksByAssignedUser(userId, PageRequest.of(page, size));
        return ResponseEntity.ok(tasks);
    }

    //Get Tasks by Project and Status (Paginated)
    @GetMapping("/project/{projectId}/status")
    public ResponseEntity<Page<Tasks>> getTasksByProjectAndStatus(
            @PathVariable Long projectId,
            @RequestParam("status") ETaskStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Tasks> tasks = taskService.getTasksByProjectAndStatus(projectId, status, PageRequest.of(page, size));
        return ResponseEntity.ok(tasks);
    }

    // Get Tasks by Status (Paginated)
    @GetMapping("/status")
    public ResponseEntity<Page<Tasks>> getTasksByStatus(
            @RequestParam("status") ETaskStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Tasks> tasks = taskService.getTasksByStatus(status, PageRequest.of(page, size));
        return ResponseEntity.ok(tasks);
    }

    //Get Tasks by Assigned User and Status (Paginated)
    @GetMapping("/user/{userId}/status")
    public ResponseEntity<Page<Tasks>> getTasksByUserAndStatus(
            @PathVariable Long userId,
            @RequestParam("status") ETaskStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Tasks> tasks = taskService.getTasksByAssignedUserAndStatus(userId, status, PageRequest.of(page, size));
        return ResponseEntity.ok(tasks);
    }

    //Update Entire Task
    @PutMapping("/{taskId}")
    public ResponseEntity<Tasks> updateTask(@PathVariable Long taskId, @RequestBody Tasks updatedTask) {
        Tasks updated = taskService.updateTask(taskId, updatedTask);
        return ResponseEntity.ok(updated);
    }

    //Update Only Status
    @PatchMapping("/{taskId}/status")
    public ResponseEntity<?> updateTaskStatus(@PathVariable Long taskId, @RequestBody Map<String, String> statusMap) {
        try {
            String statusStr = statusMap.get("status");
            if (statusStr == null) {
                return ResponseEntity.badRequest().body("Missing 'status' field in request body.");
            }
            ETaskStatus newStatus = ETaskStatus.valueOf(statusStr.toUpperCase());
            Tasks updated = taskService.updateTaskStatus(taskId, newStatus);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //Delete Task
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @PreAuthorize("hasRole('PROJECT_MANAGER')")
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskDTO request) {
        try {
            Tasks task = taskService.createTaskFromRequest(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(task);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
