package com.alice.syncly.project.service;

import com.alice.syncly.member.domain.Member;
import com.alice.syncly.member.repository.MemberRepository;
import com.alice.syncly.project.domain.ParticipationStatus;
import com.alice.syncly.project.domain.Project;
import com.alice.syncly.project.domain.ProjectMember;
import com.alice.syncly.project.domain.ProjectRole;
import com.alice.syncly.project.repository.ProjectMemberRepository;
import com.alice.syncly.project.repository.ProjectRepository;
import com.alice.syncly.project.repository.ProjectScheduleRepository;
import com.alice.syncly.project.web.dto.ProjectListItemDto;
import com.alice.syncly.slack.service.SlackNotifierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ProjectService {

    private static final Logger log = LoggerFactory.getLogger(ProjectService.class);

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectScheduleRepository projectScheduleRepository;
    private final MemberRepository memberRepository;
    private final SlackNotifierService slackNotifierService;

    public ProjectService(ProjectRepository projectRepository,
                          ProjectMemberRepository projectMemberRepository,
                          ProjectScheduleRepository projectScheduleRepository,
                          MemberRepository memberRepository,
                          SlackNotifierService slackNotifierService) {
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.projectScheduleRepository = projectScheduleRepository;
        this.memberRepository = memberRepository;
        this.slackNotifierService = slackNotifierService;
    }

    @Transactional
    public Project createProject(String name, String description, LocalDate startDate, LocalDate endDate,
                                 Long ownerId, List<Long> invitedMemberIds) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("마감일은 시작일 이후여야 합니다.");
        }

        Member owner = memberRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found: " + ownerId));

        Project project = new Project(name, description, owner);
        project.setStartDate(startDate);
        project.setEndDate(endDate);
        projectRepository.save(project);

        // 생성자를 PM으로 project_member에 추가
        projectMemberRepository.save(
                new ProjectMember(project, owner, ProjectRole.PM, ParticipationStatus.ACTIVE)
        );

        // 초대 멤버를 MEMBER로 추가 (생성자 중복 제외)
        List<Member> invitees = new ArrayList<>();
        for (Long memberId : invitedMemberIds) {
            if (memberId.equals(ownerId)) continue;
            memberRepository.findById(memberId).ifPresent(invited -> {
                projectMemberRepository.save(
                        new ProjectMember(project, invited, ProjectRole.MEMBER, ParticipationStatus.ACTIVE)
                );
                invitees.add(invited);
            });
        }

        if (!invitees.isEmpty()) {
            slackNotifierService.sendProjectInvitations(
                    owner.getName(), owner.getSlackUserId(), project.getName(), invitees);
        }

        return project;
    }

    public List<ProjectListItemDto> findProjectListByMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found: " + memberId));

        List<ProjectMember> projectMembers = projectMemberRepository.findByMember(member);

        return projectMembers.stream().map(pm -> {
            Project project = pm.getProject();

            long total = projectScheduleRepository.countByProject(project);
            int progress = 0;
            if (total > 0) {
                long done = projectScheduleRepository.countByProjectAndStatus(project, "DONE");
                progress = (int) (done * 100 / total);
            }

            int memberCount = projectMemberRepository.findByProject(project).size();

            return new ProjectListItemDto(project, project.getOwner().getName(), memberCount, progress, true);
        }).toList();
    }

    public List<ProjectListItemDto> searchProjects(String q, Long memberId) {
        List<Project> projects = (q == null || q.isBlank())
                ? projectRepository.findAll()
                : projectRepository.findByNameContainingIgnoreCase(q);

        return projects.stream().map(project -> {
            long total = projectScheduleRepository.countByProject(project);
            int progress = 0;
            if (total > 0) {
                long done = projectScheduleRepository.countByProjectAndStatus(project, "DONE");
                progress = (int) (done * 100 / total);
            }
            int memberCount = projectMemberRepository.findByProject(project).size();
            boolean joined = projectMemberRepository.existsByProjectIdAndMemberId(project.getId(), memberId);
            return new ProjectListItemDto(project, project.getOwner().getName(), memberCount, progress, joined);
        }).toList();
    }

    public Optional<Project> findById(Long id) {
        return projectRepository.findById(id);
    }

    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    public List<Project> searchByName(String name) {
        return projectRepository.findByNameContainingIgnoreCase(name);
    }
}
