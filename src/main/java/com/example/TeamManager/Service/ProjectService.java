package com.example.TeamManager.Service;

import com.example.TeamManager.Model.Projects;
import com.example.TeamManager.Model.Users;
import com.example.TeamManager.Repository.ProjectRepository;
import com.example.TeamManager.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository,UserRepository userRepository){
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public Projects CreateProject(Projects projects){
        if(projects.getName() == null || projects.getName().trim().isEmpty()){
            throw new IllegalArgumentException("Project Name cannot be empty");
        }

        if(projects.getDescription() == null || projects.getDescription().isEmpty()){
            throw new IllegalArgumentException("Project Description cannot be empty");
        }

        return projectRepository.save(projects);
    }

    public List<Projects> getAllProjects(){
        return projectRepository.findAll();
    }

    public Optional<Projects> getProjectById(Long id) {
        return projectRepository.findById(id);
    }

    public Optional<Projects> getProjectsByManager(Long managerId){
        return userRepository.findById(managerId)
                .flatMap(projectRepository::findByProjectManager);
        //return projectRepository.findByProjectManager(managerId);
    }

    public Projects updateProject(Long id, Projects updatedProject) {
        Projects existing = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with ID: " + id));

        if (updatedProject.getName() != null) {
            existing.setName(updatedProject.getName());
        }
        if (updatedProject.getDescription() != null) {
            existing.setDescription(updatedProject.getDescription());
        }
        if (updatedProject.getStartDate() != null) {
            existing.setStartDate(updatedProject.getStartDate());
        }
        if (updatedProject.getEndDate() != null) {
            existing.setEndDate(updatedProject.getEndDate());
        }
        if (updatedProject.getProjectManager() != null && updatedProject.getProjectManager().getId() != null) {
            Users manager = userRepository.findById(updatedProject.getProjectManager().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Project Manager not found with ID: " + updatedProject.getProjectManager().getId()));
            existing.setProjectManager(manager);
        }

        return projectRepository.save(existing);
    }

    public void deleteProject(Long id){
        if(!projectRepository.existsById(id)){
            throw new IllegalArgumentException("Project not found with ID: " + id);
        }

        projectRepository.deleteById(id);
    }
}
