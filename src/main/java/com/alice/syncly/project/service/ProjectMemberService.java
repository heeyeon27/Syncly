package com.alice.syncly.project.service;

import com.alice.syncly.member.domain.Member;
import com.alice.syncly.member.repository.MemberRepository;
import com.alice.syncly.project.domain.Project;
import com.alice.syncly.project.domain.ProjectMember;
import com.alice.syncly.project.domain.ProjectRole;
import com.alice.syncly.project.repository.ProjectMemberRepository;
import com.alice.syncly.project.repository.ProjectRepository;
import com.alice.syncly.slack.service.SlackNotifierService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final SlackNotifierService slackNotifierService;

    public ProjectMemberService(ProjectMemberRepository projectMemberRepository,
                                ProjectRepository projectRepository,
                                MemberRepository memberRepository,
                                SlackNotifierService slackNotifierService) {
        this.projectMemberRepository = projectMemberRepository;
        this.projectRepository = projectRepository;
        this.memberRepository = memberRepository;
        this.slackNotifierService = slackNotifierService;
    }

    @Transactional
    public ProjectMember addMemberToProject(Long projectId, Long memberId, ProjectRole projectRole) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found: " + memberId));

        ProjectMember projectMember = new ProjectMember(project, member, projectRole);
        projectMemberRepository.save(projectMember);

        slackNotifierService.sendProjectInvitation(member.getName(), project.getName());

        return projectMember;
    }

    public List<ProjectMember> findByProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
        return projectMemberRepository.findByProject(project);
    }

    public List<ProjectMember> findByMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found: " + memberId));
        return projectMemberRepository.findByMember(member);
    }
}
