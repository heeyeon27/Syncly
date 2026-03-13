package com.alice.syncly.project.web;

import com.alice.syncly.project.domain.ProjectMember;
import com.alice.syncly.project.service.ProjectMemberService;
import com.alice.syncly.project.web.dto.ProjectMemberCreateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/project-members")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    public ProjectMemberController(ProjectMemberService projectMemberService) {
        this.projectMemberService = projectMemberService;
    }

    @PostMapping
    public ResponseEntity<ProjectMember> create(@RequestBody ProjectMemberCreateRequest request) {
        ProjectMember projectMember = projectMemberService.addMemberToProject(
                request.getProjectId(),
                request.getMemberId(),
                request.getProjectRole()
        );
        return ResponseEntity
                .created(URI.create("/api/project-members/" + projectMember.getId()))
                .body(projectMember);
    }

    @GetMapping("/by-project/{projectId}")
    public List<ProjectMember> findByProject(@PathVariable Long projectId) {
        return projectMemberService.findByProject(projectId);
    }

    @GetMapping("/by-member/{memberId}")
    public List<ProjectMember> findByMember(@PathVariable Long memberId) {
        return projectMemberService.findByMember(memberId);
    }
}
