package com.alice.syncly.project.web;

import com.alice.syncly.project.domain.Project;
import com.alice.syncly.project.service.ProjectService;
import com.alice.syncly.project.web.dto.ProjectCreateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<Project> create(@RequestBody ProjectCreateRequest request) {
        Project project = projectService.createProject(
                request.getName(),
                request.getDescription(),
                request.getOwnerId()
        );
        return ResponseEntity
                .created(URI.create("/api/projects/" + project.getId()))
                .body(project);
    }

    @GetMapping
    public List<Project> findAll() {
        return projectService.findAll();
    }
}
