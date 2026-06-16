package com.alice.syncly.dashboard.web;

import com.alice.syncly.auth.service.MemberUserDetails;
import com.alice.syncly.issue.service.IssueService;
import com.alice.syncly.issue.web.dto.IssueItemDto;
import com.alice.syncly.member.domain.Member;
import com.alice.syncly.notice.service.NoticeService;
import com.alice.syncly.notice.web.dto.NoticeItemDto;
import com.alice.syncly.project.domain.ProjectMember;
import com.alice.syncly.project.domain.ProjectStatus;
import com.alice.syncly.project.service.ProjectMemberService;
import com.alice.syncly.project.domain.Project;
import com.alice.syncly.project.service.ProjectScheduleService;
import com.alice.syncly.project.service.ProjectService;
import com.alice.syncly.project.web.dto.ProjectScheduleItemDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    private final ProjectService projectService;
    private final ProjectMemberService projectMemberService;
    private final NoticeService noticeService;
    private final IssueService issueService;
    private final ProjectScheduleService projectScheduleService;

    public DashboardController(ProjectService projectService,
                               ProjectMemberService projectMemberService,
                               NoticeService noticeService,
                               IssueService issueService,
                               ProjectScheduleService projectScheduleService) {
        this.projectService = projectService;
        this.projectMemberService = projectMemberService;
        this.noticeService = noticeService;
        this.issueService = issueService;
        this.projectScheduleService = projectScheduleService;
    }

    @GetMapping({"/", "/dashboard"})
    public String index(@AuthenticationPrincipal MemberUserDetails userDetails,
                        @RequestParam(required = false) String q,
                        Model model) {

        int totalProjects = projectService.findAll().size();
        model.addAttribute("totalProjects", totalProjects);
        model.addAttribute("waitingCount", 0);

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

            // [6] 긴급 이슈 카운트 (담당자 본인, DONE 제외)
            long urgentCount = issueService.countUrgentByAssignee(member.getId());
            model.addAttribute("urgentCount", urgentCount);

            // [7] 이번 주 마감작업 (내 담당 작업만)
            List<ProjectScheduleItemDto> thisWeekTasks = projectScheduleService.findThisWeekSchedules(member.getId());
            model.addAttribute("thisWeekTasks", thisWeekTasks);

            // [8] 지연된 작업 (내 담당, 종료일 초과 미완료)
            List<ProjectScheduleItemDto> overdueTasks = projectScheduleService.findOverdueSchedules(member.getId());
            model.addAttribute("overdueTasks", overdueTasks);
        } else {
            model.addAttribute("inProgressCount", 0);
            model.addAttribute("urgentCount", 0);
            model.addAttribute("unreadNotices", Collections.emptyList());
            model.addAttribute("myIssues", Collections.emptyList());
            model.addAttribute("thisWeekTasks", Collections.emptyList());
            model.addAttribute("overdueTasks", Collections.emptyList());
        }

        model.addAttribute("memberProjectIds", memberProjectIds);

        if (q != null && !q.isBlank()) {
            List<Project> searchResults = projectService.searchByName(q);
            model.addAttribute("searchResults", searchResults);
        }

        return "index";
    }
}
