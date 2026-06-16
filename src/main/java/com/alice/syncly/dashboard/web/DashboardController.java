package com.alice.syncly.dashboard.web;

import com.alice.syncly.auth.service.MemberUserDetails;
import com.alice.syncly.issue.service.IssueService;
import com.alice.syncly.issue.web.dto.IssueItemDto;
import com.alice.syncly.member.domain.Member;
import com.alice.syncly.notice.service.NoticeService;
import com.alice.syncly.notice.web.dto.NoticeItemDto;
import com.alice.syncly.project.domain.Project;
import com.alice.syncly.project.domain.ProjectMember;
import com.alice.syncly.project.domain.ProjectStatus;
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
    private final NoticeService noticeService;
    private final IssueService issueService;

    public DashboardController(ProjectService projectService,
                               ProjectMemberService projectMemberService,
                               NoticeService noticeService,
                               IssueService issueService) {
        this.projectService = projectService;
        this.projectMemberService = projectMemberService;
        this.noticeService = noticeService;
        this.issueService = issueService;
    }

    @GetMapping({"/", "/dashboard"})
    public String index(@AuthenticationPrincipal MemberUserDetails userDetails,
                        @RequestParam(required = false) String q,
                        Model model) {

        List<Project> allProjects = projectService.findAll();

        model.addAttribute("totalProjects", allProjects.size());
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
            List<ProjectMember> myProjectMembers = projectMemberService.findByMember(member.getId())
                    .stream()
                    .filter(pm -> pm.getProject().getStatus() != ProjectStatus.COMPLETED)
                    .collect(Collectors.toList());
            model.addAttribute("myProjectMembers", myProjectMembers);
            model.addAttribute("inProgressCount", myProjectMembers.size());
            memberProjectIds = myProjectMembers.stream()
                    .map(pm -> pm.getProject().getId())
                    .collect(Collectors.toSet());

            List<NoticeItemDto> unreadNotices = noticeService.findUnreadByMember(member.getId());
            model.addAttribute("unreadNotices", unreadNotices);

            List<IssueItemDto> myIssues = issueService.findByAssigneeExcludingDone(member.getId());
            model.addAttribute("myIssues", myIssues);
        } else {
            model.addAttribute("inProgressCount", 0);
            model.addAttribute("unreadNotices", Collections.emptyList());
            model.addAttribute("myIssues", Collections.emptyList());
        }

        model.addAttribute("memberProjectIds", memberProjectIds);

        if (q != null && !q.isBlank()) {
            List<Project> searchResults = projectService.searchByName(q);
            model.addAttribute("searchResults", searchResults);
        }

        return "index";
    }
}
