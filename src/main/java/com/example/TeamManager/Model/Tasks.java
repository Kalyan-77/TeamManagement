package com.example.TeamManager.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
public class Tasks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title should be entered")
    @Size(min = 5, max = 200, message = "Title must contain at least 5 characters and less than 200 characters")
    private String title;

    @NotBlank(message = "Enter Description")
    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ETaskStatus status = ETaskStatus.TO_DO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ETaskPriority priority = ETaskPriority.MEDIUM;

    @FutureOrPresent(message = "Due date must be today or a future date")
    private LocalDateTime dueDate;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Projects project;

    @ManyToOne
    @JoinColumn(name = "assigned_to_id")
    private Users assignedTo;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}