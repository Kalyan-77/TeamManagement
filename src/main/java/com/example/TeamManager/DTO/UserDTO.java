package com.example.TeamManager.DTO;

import lombok.Data;

import java.util.Set;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private Set<String> roles; // ROLE_TEAM_MEMBER, ROLE_PROJECT_MANAGER
}
