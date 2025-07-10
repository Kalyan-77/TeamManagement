package com.example.TeamManager.Service;

import com.example.TeamManager.Model.ETaskPriority;
import com.example.TeamManager.Model.ETaskStatus;
import com.example.TeamManager.Model.Tasks;
import com.example.TeamManager.Model.Users;
import com.example.TeamManager.Repository.ProjectRepository;
import com.example.TeamManager.Repository.TasksRepository;
import com.example.TeamManager.Repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TaskService {
    private final TasksRepository tasksRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public TaskService(TasksRepository tasksRepository, ProjectRepository projectRepository, UserRepository userRepository){
        this.tasksRepository = tasksRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }
    public Tasks createTask(Tasks task) {
        // Validate title
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }

        // Validate due date
        if (task.getDueDate() != null && task.getDueDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Due date must be today or a future date");
        }

        //validate project
        if(task.getProject() != null && task.getProject().getId() == null || !projectRepository.existsById(task.getProject().getId())){
            throw new IllegalArgumentException("Project not found with Id");
        }

        // Handle assignedTo user
        if (task.getAssignedTo() != null && task.getAssignedTo().getId() != null) {
            Long assignedToId = task.getAssignedTo().getId();
            task.setAssignedTo(userRepository.findById(assignedToId)
                    .orElseThrow(() -> new IllegalArgumentException("Assigned user not found with id: " + assignedToId)));
        } else {
            task.setAssignedTo(null); // unassigned task
        }

        // Set default status if null
        if (task.getStatus() == null) {
            task.setStatus(ETaskStatus.TO_DO);
        }

        // Set default priority if null
        if (task.getPriority() == null) {
            task.setPriority(ETaskPriority.MEDIUM);
        }

        //Save and return task
        return tasksRepository.save(task);

    }

    //Get a task ny Id
    public Optional<Tasks> getTaskById(Long id){
        return tasksRepository.findById(id);
    }

    //Get All tasks with pagination
    public Page<Tasks> getAllTasks(Pageable pageable){
        return tasksRepository.findAll(pageable);
    }

    //get tasks assigned to a specific user with pagination
    public Page<Tasks> getTaskAssigned(Long id, Pageable pageable){
        return tasksRepository.findByAssignedId(id,pageable);
    }

    //method to find tasks by projects and status with pagination(already present, just confirming)
    public Page<Tasks> getTasksByProjectAndStatus(Long projectId, ETaskStatus status, Pageable pageable){
        return tasksRepository.findByProjectIdAndStatus(projectId,status,pageable);
    }

    //Method to find tasks by status with pagination(newly added)
    public Page<Tasks> getTasksByStatus(ETaskStatus status, Pageable pageable){
        return tasksRepository.findByStatus(status,pageable);
    }

    //Get Tasks for a specific projects with pagination
    public Page<Tasks> getTasksByProject(Long projectId, Pageable pageable){
        return tasksRepository.findByProjectId(projectId,pageable);
    }
    //Method to find tasks by asingned user and status with pagination
    public Page<Tasks> findByAssignedUserAndStatus(Long userId,ETaskStatus status, Pageable pageable){
        return tasksRepository.findByAssignedIdAndStatus(userId, status, pageable);
    }
    //Delete a task
    public void DeleteTask(Long id){
        if(!tasksRepository.existsById(id)){
            throw new IllegalArgumentException("Task not found by is: " + id);
        }
        tasksRepository.deleteById(id);
    }

    //Update an existing task
    public Tasks updateTask(Long id, Tasks updatedTask) {
        Tasks existingTask = tasksRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + id));

        // Title
        if (updatedTask.getTitle() != null && !updatedTask.getTitle().trim().isEmpty()) {
            existingTask.setTitle(updatedTask.getTitle());
        }

        // Description
        if (updatedTask.getDescription() != null && !updatedTask.getDescription().trim().isEmpty()) {
            existingTask.setDescription(updatedTask.getDescription());
        }

        // Status
        if (updatedTask.getStatus() != null) {
            existingTask.setStatus(updatedTask.getStatus());
        }

        // Priority
        if (updatedTask.getPriority() != null) {
            existingTask.setPriority(updatedTask.getPriority());
        }

        //DueDate
        if(updatedTask.getDueDate() != null){
            existingTask.setDueDate(updatedTask.getDueDate());
        }

        //Handle Task Assignment
        if(updatedTask.getAssignedTo() != null){
            if(updatedTask.getAssignedTo().getId() != null){
                Long newAssignedId = updatedTask.getAssignedTo().getId();
                Users newAssignee = userRepository.findById(newAssignedId)
                        .orElseThrow(() -> new IllegalArgumentException("New Assigned user not found id: " + newAssignedId));
                existingTask.setAssignedTo(newAssignee);
            }else{
                existingTask.setAssignedTo(null);
            }
        }
        return tasksRepository.save(existingTask);
    }

    //Update only the status of a Task
    public Tasks UpdateTaskByStatus(Long taskId, ETaskStatus status){
        Tasks tasks = tasksRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + taskId));
        tasks.setStatus(status);
        return tasksRepository.save(tasks);
    }
}
