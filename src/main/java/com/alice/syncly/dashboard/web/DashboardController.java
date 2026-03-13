package com.alice.syncly.dashboard.web;

import com.alice.syncly.auth.service.MemberUserDetails;
import com.alice.syncly.member.domain.Member;
import com.alice.syncly.project.domain.Project;
import com.alice.syncly.project.domain.ProjectMember;
import com.alice.syncly.project.service.ProjectMemberService;
import com.alice.syncly.project.service.ProjectService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    private final ProjectService projectService;
    private final ProjectMemberService projectMemberService;

    public DashboardController(ProjectService projectService, ProjectMemberService projectMemberService) {
        this.projectService = projectService;
        this.projectMemberService = projectMemberService;
    }

    @GetMapping({"/", "/dashboard"})
    public String index(@AuthenticationPrincipal MemberUserDetails userDetails,
                        @RequestParam(required = false) String q,
                        Model model) {

        List<Project> allProjects = projectService.findAll();

        model.addAttribute("totalProjects", allProjects.size());
        model.addAttribute("inProgressCount", 0);
        model.addAttribute("waitingCount", 0);
        model.addAttribute("urgentCount", 0);

        List<Project> recentProjects = allProjects.stream()
                .sorted(Comparator.comparing(Project::getCreatedAt).reversed())
                .limit(5)
                .collect(Collectors.toList());
        model.addAttribute("recentProjects", recentProjects);

        Set<Long> memberProjectIds = Collections.emptySet();

        if (userDetails != null) {
            Member member = userDetails.getMember();
            List<ProjectMember> myProjectMembers = projectMemberService.findByMember(member.getId());
            model.addAttribute("myProjectMembers", myProjectMembers);
            memberProjectIds = myProjectMembers.stream()
                    .map(pm -> pm.getProject().getId())
                    .collect(Collectors.toSet());
        }

        model.addAttribute("memberProjectIds", memberProjectIds);

        if (q != null && !q.isBlank()) {
            List<Project> searchResults = projectService.searchByName(q);
            model.addAttribute("searchResults", searchResults);
        }

        return "index";
    }
}
