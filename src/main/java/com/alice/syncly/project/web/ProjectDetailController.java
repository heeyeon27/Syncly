package com.alice.syncly.project.web;

import com.alice.syncly.auth.service.MemberUserDetails;
import com.alice.syncly.issue.service.IssueService;
import com.alice.syncly.issue.web.dto.IssueItemDto;
import com.alice.syncly.notice.service.NoticeService;
import com.alice.syncly.notice.web.dto.NoticeItemDto;
import com.alice.syncly.project.domain.ProjectMember;
import com.alice.syncly.project.service.ProjectScheduleService;
import com.alice.syncly.project.web.dto.ProjectDetailDto;
import com.alice.syncly.project.web.dto.ProjectScheduleItemDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/projects")
public class ProjectDetailController {

    private final ProjectScheduleService projectScheduleService;
    private final IssueService issueService;
    private final NoticeService noticeService;

    public ProjectDetailController(ProjectScheduleService projectScheduleService,
                                   IssueService issueService,
                                   NoticeService noticeService) {
        this.projectScheduleService = projectScheduleService;
        this.issueService = issueService;
        this.noticeService = noticeService;
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model,
                         @AuthenticationPrincipal MemberUserDetails userDetails) {
        ProjectDetailDto project               = projectScheduleService.findProjectDetail(id);
        List<ProjectScheduleItemDto> schedules = projectScheduleService.findSchedules(id);
        List<ProjectMember> members            = projectScheduleService.findProjectMembers(id);
        List<IssueItemDto> issues              = issueService.findByProject(id);
        List<NoticeItemDto> notices            = noticeService.findByProject(id);
        Long currentMemberId = userDetails != null ? userDetails.getMember().getId() : null;
        model.addAttribute("project", project);
        model.addAttribute("schedules", schedules);
        model.addAttribute("members", members);
        model.addAttribute("issues", issues);
        model.addAttribute("notices", notices);
        model.addAttribute("currentMemberId", currentMemberId);
        return "project-detail";
    }
}
