package com.alice.syncly.issue.web;

import com.alice.syncly.auth.service.MemberUserDetails;
import com.alice.syncly.issue.domain.Issue;
import com.alice.syncly.issue.service.IssueService;
import com.alice.syncly.issue.web.dto.IssueCreateRequest;
import com.alice.syncly.issue.web.dto.IssueResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/issues")
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @PostMapping
    public ResponseEntity<IssueResponse> create(
            @RequestBody IssueCreateRequest request,
            @AuthenticationPrincipal MemberUserDetails userDetails) {

        Issue issue = issueService.create(
                request.getProjectId(),
                userDetails.getMember().getId(),
                request.getAssigneeId(),
                request.getTitle(),
                request.getDescription(),
                request.getPriority(),
                request.getDueDate(),
                request.getScheduleId()
        );
        return ResponseEntity
                .created(URI.create("/api/issues/" + issue.getId()))
                .body(new IssueResponse(issue));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal MemberUserDetails userDetails) {
        issueService.updateStatus(id, userDetails.getMember().getId(), body.get("status"));
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, String>> handleBadRequest(RuntimeException e) {
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }
}
