package com.alice.syncly.issue.service;

import com.alice.syncly.issue.domain.Issue;
import com.alice.syncly.issue.domain.IssuePriority;
import com.alice.syncly.issue.domain.IssueStatus;
import com.alice.syncly.issue.repository.IssueRepository;
import com.alice.syncly.issue.web.dto.IssueItemDto;
import com.alice.syncly.member.domain.Member;
import com.alice.syncly.member.repository.MemberRepository;
import com.alice.syncly.project.domain.Project;
import com.alice.syncly.project.domain.ProjectSchedule;
import com.alice.syncly.project.repository.ProjectRepository;
import com.alice.syncly.project.repository.ProjectScheduleRepository;
import com.alice.syncly.slack.service.SlackNotifierService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class IssueService {

    private final IssueRepository issueRepository;
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final ProjectScheduleRepository scheduleRepository;
    private final SlackNotifierService slackNotifierService;

    public IssueService(IssueRepository issueRepository,
                        ProjectRepository projectRepository,
                        MemberRepository memberRepository,
                        ProjectScheduleRepository scheduleRepository,
                        SlackNotifierService slackNotifierService) {
        this.issueRepository      = issueRepository;
        this.projectRepository    = projectRepository;
        this.memberRepository     = memberRepository;
        this.scheduleRepository   = scheduleRepository;
        this.slackNotifierService = slackNotifierService;
    }

    @Transactional
    public Issue create(Long projectId, Long reporterId, Long assigneeId,
                        String title, String description, String priority, LocalDate dueDate,
                        Long scheduleId) {
        if (assigneeId == null) {
            throw new IllegalArgumentException("담당자를 선택하세요.");
        }
        Project project  = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
        Member reporter  = memberRepository.findById(reporterId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found: " + reporterId));
        Member assignee  = memberRepository.findById(assigneeId)
                .orElseThrow(() -> new IllegalArgumentException("Assignee not found: " + assigneeId));

        Issue issue = new Issue(title, description, project, reporter,
                IssuePriority.valueOf(priority), IssueStatus.TODO);
        issue.setAssignee(assignee);
        if (dueDate != null) {
            issue.setDueDate(dueDate);
        }
        if (scheduleId != null) {
            ProjectSchedule schedule = scheduleRepository.findById(scheduleId).orElse(null);
            issue.setSchedule(schedule);
        }

        Issue saved = issueRepository.save(issue);

        slackNotifierService.sendIssueAssigned(
                project.getName(), title,
                assignee.getName(), assignee.getSlackUserId()
        );

        return saved;
    }

    public List<IssueItemDto> findByProject(Long projectId) {
        return issueRepository.findByProjectIdWithMembers(projectId)
                .stream()
                .sorted(Comparator.comparingInt(i -> switch (i.getPriority()) {
                    case URGENT -> 0;
                    case HIGH   -> 1;
                    case MEDIUM -> 2;
                    case LOW    -> 3;
                }))
                .map(IssueItemDto::new)
                .collect(Collectors.toList());
    }

    public List<IssueItemDto> findByAssigneeExcludingDone(Long memberId) {
        return issueRepository.findByAssigneeIdExcludingStatus(memberId, IssueStatus.DONE)
                .stream()
                .sorted(Comparator.comparingInt(i -> switch (i.getPriority()) {
                    case URGENT -> 0;
                    case HIGH   -> 1;
                    case MEDIUM -> 2;
                    case LOW    -> 3;
                }))
                .map(IssueItemDto::new)
                .collect(Collectors.toList());
    }

    public long countUrgentByAssignee(Long memberId) {
        return issueRepository.countByAssigneeAndPriorityExcludingStatus(
                memberId, IssuePriority.URGENT, IssueStatus.DONE);
    }

    @Transactional
    public void updateStatus(Long issueId, Long memberId, String newStatus) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new IllegalArgumentException("이슈를 찾을 수 없습니다."));
        if (issue.getAssignee() == null || !issue.getAssignee().getId().equals(memberId)) {
            throw new IllegalStateException("담당자만 상태를 변경할 수 있습니다.");
        }
        issue.setStatus(IssueStatus.valueOf(newStatus));
    }
}
