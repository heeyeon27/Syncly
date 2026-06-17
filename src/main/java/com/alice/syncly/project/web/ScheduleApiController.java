package com.alice.syncly.project.web;

import com.alice.syncly.auth.service.MemberUserDetails;
import com.alice.syncly.project.domain.ProjectMember;
import com.alice.syncly.project.service.ProjectScheduleService;
import com.alice.syncly.project.web.dto.PmsRegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api")
public class ScheduleApiController {

    private final ProjectScheduleService projectScheduleService;

    public ScheduleApiController(ProjectScheduleService projectScheduleService) {
        this.projectScheduleService = projectScheduleService;
    }

    @PostMapping("/pms/register")
    public ResponseEntity<Void> register(@RequestBody PmsRegisterRequest request) {
        projectScheduleService.register(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/schedules/{id}/complete")
    public ResponseEntity<Void> complete(@PathVariable Long id) {
        projectScheduleService.complete(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/schedules/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id,
                                             @RequestBody Map<String, String> body) {
        projectScheduleService.updateStatus(id, body.get("status"));
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/schedules/{id}")
    public ResponseEntity<Void> updateSchedule(@PathVariable Long id,
                                               @RequestBody Map<String, String> body,
                                               @AuthenticationPrincipal MemberUserDetails userDetails) {
        LocalDate startDate = body.get("startDate") != null && !body.get("startDate").isBlank()
                ? LocalDate.parse(body.get("startDate")) : null;
        LocalDate endDate = body.get("endDate") != null && !body.get("endDate").isBlank()
                ? LocalDate.parse(body.get("endDate")) : null;
        Long newProjectMemberId = body.get("projectMemberId") != null && !body.get("projectMemberId").isBlank()
                ? Long.parseLong(body.get("projectMemberId")) : null;
        projectScheduleService.updateSchedule(id, userDetails.getMember().getId(),
                body.get("phaseName"), body.get("title"), startDate, endDate,
                body.get("roleType"), newProjectMemberId, body.get("memo"));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/schedules/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id,
                                               @AuthenticationPrincipal MemberUserDetails userDetails) {
        projectScheduleService.deleteSchedule(id, userDetails.getMember().getId());
        return ResponseEntity.ok().build();
    }

    /** [2] 프로젝트 소속 멤버 목록 (project_member_id 기준) */
    @GetMapping("/projects/{projectId}/members")
    public ResponseEntity<List<Map<String, Object>>> getProjectMembers(@PathVariable Long projectId) {
        List<ProjectMember> members = projectScheduleService.findProjectMembers(projectId);
        List<Map<String, Object>> result = members.stream().map(pm -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id",    pm.getId());
            m.put("name",  pm.getMember().getName());
            m.put("email", pm.getMember().getEmail());
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, String>> handleBadRequest(RuntimeException e) {
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }
}
