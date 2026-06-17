package com.alice.syncly.project.web;

import com.alice.syncly.auth.service.MemberUserDetails;
import com.alice.syncly.project.domain.ProjectMember;
import com.alice.syncly.project.service.ProjectMemberService;
import com.alice.syncly.project.web.dto.ProjectMemberCreateRequest;
import com.alice.syncly.project.web.dto.ProjectMemberResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public ResponseEntity<ProjectMemberResponse> create(@RequestBody ProjectMemberCreateRequest request) {
        ProjectMember projectMember = projectMemberService.addMemberToProject(
                request.getProjectId(),
                request.getMemberId(),
                request.getProjectRole()
        );
        return ResponseEntity
                .created(URI.create("/api/project-members/" + projectMember.getId()))
                .body(new ProjectMemberResponse(projectMember));
    }

    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    public ResponseEntity<Map<String, String>> handleBadRequest(RuntimeException e) {
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeMember(@PathVariable Long id,
                                             @AuthenticationPrincipal MemberUserDetails userDetails) {
        projectMemberService.removeMember(id, userDetails.getMember().getId());
        return ResponseEntity.ok().build();
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
