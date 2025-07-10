package com.example.TeamManager.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long projectManagerId;
    private LocalDateTime createdAt;
}
