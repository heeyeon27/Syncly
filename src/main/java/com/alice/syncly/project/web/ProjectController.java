package com.alice.syncly.project.web;

import com.alice.syncly.auth.service.MemberUserDetails;
import com.alice.syncly.project.domain.Project;
import com.alice.syncly.project.service.ProjectService;
import com.alice.syncly.project.web.dto.ProjectCreateRequest;
import com.alice.syncly.project.web.dto.ProjectResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<ProjectResponse> create(
            @RequestBody ProjectCreateRequest request,
            @AuthenticationPrincipal MemberUserDetails userDetails) {

        Long ownerId = userDetails.getMember().getId();
        Project project = projectService.createProject(
                request.getName(),
                request.getDescription(),
                request.getStartDate(),
                request.getEndDate(),
                ownerId,
                request.getInvitedMemberIds()
        );
        return ResponseEntity
                .created(URI.create("/api/projects/" + project.getId()))
                .body(new ProjectResponse(project));
    }

    @GetMapping
    public List<Project> findAll() {
        return projectService.findAll();
    }
}
