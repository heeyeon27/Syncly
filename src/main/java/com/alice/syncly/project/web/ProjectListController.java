package com.alice.syncly.project.web;

import com.alice.syncly.auth.service.MemberUserDetails;
import com.alice.syncly.project.service.ProjectService;
import com.alice.syncly.project.web.dto.ProjectListItemDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/projects")
public class ProjectListController {

    private final ProjectService projectService;

    public ProjectListController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public String list(@AuthenticationPrincipal MemberUserDetails userDetails,
                       Model model) {
        Long memberId = userDetails.getMember().getId();
        model.addAttribute("projects", projectService.findProjectListByMember(memberId));
        return "project-list";
    }

    @GetMapping("/search")
    public String search(@AuthenticationPrincipal MemberUserDetails userDetails,
                         @RequestParam(required = false) String q,
                         Model model) {
        Long memberId = userDetails.getMember().getId();
        model.addAttribute("projects", projectService.searchProjects(q, memberId));
        model.addAttribute("searchQuery", q != null ? q : "");
        return "project-list";
    }
}
