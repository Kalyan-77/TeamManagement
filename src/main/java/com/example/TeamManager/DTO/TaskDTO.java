package com.example.TeamManager.DTO;

import com.example.TeamManager.Model.ETaskPriority;
import com.example.TeamManager.Model.ETaskStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private ETaskStatus status;
    private ETaskPriority priority;
    private LocalDateTime dueDate;
    private Long projectId;
    private Long assignedToId;
    private LocalDateTime createdAt;
}
