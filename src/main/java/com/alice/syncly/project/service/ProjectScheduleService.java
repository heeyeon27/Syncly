package com.alice.syncly.project.service;

import com.alice.syncly.issue.domain.IssueStatus;
import com.alice.syncly.issue.repository.IssueRepository;
import com.alice.syncly.project.domain.Project;
import com.alice.syncly.project.domain.ProjectMember;
import com.alice.syncly.project.domain.ProjectSchedule;
import com.alice.syncly.project.domain.ProjectStatus;
import com.alice.syncly.project.repository.ProjectMemberRepository;
import com.alice.syncly.project.repository.ProjectRepository;
import com.alice.syncly.project.repository.ProjectScheduleRepository;
import com.alice.syncly.project.web.dto.PmsRegisterRequest;
import com.alice.syncly.project.web.dto.ProjectDetailDto;
import com.alice.syncly.project.web.dto.ProjectScheduleItemDto;
import com.alice.syncly.slack.service.SlackNotifierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ProjectScheduleService {

    private static final Logger log = LoggerFactory.getLogger(ProjectScheduleService.class);

    private final ProjectRepository projectRepository;
    private final ProjectScheduleRepository scheduleRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final IssueRepository issueRepository;
    private final SlackNotifierService slackNotifierService;

    public ProjectScheduleService(ProjectRepository projectRepository,
                                  ProjectScheduleRepository scheduleRepository,
                                  ProjectMemberRepository projectMemberRepository,
                                  IssueRepository issueRepository,
                                  SlackNotifierService slackNotifierService) {
        this.projectRepository       = projectRepository;
        this.scheduleRepository      = scheduleRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.issueRepository         = issueRepository;
        this.slackNotifierService    = slackNotifierService;
    }

    public ProjectDetailDto findProjectDetail(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));

        int memberCount = projectMemberRepository.findByProject(project).size();

        long total = scheduleRepository.countByProject(project);
        int progress = 0;
        if (total > 0) {
            long done = scheduleRepository.countByProjectAndStatus(project, "DONE");
            progress = (int) (done * 100 / total);
        }

        return new ProjectDetailDto(project, memberCount, progress);
    }

    public List<ProjectMember> findProjectMembers(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
        return projectMemberRepository.findByProjectWithMember(project);
    }

    public List<ProjectScheduleItemDto> findSchedules(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));

        List<ProjectSchedule> schedules =
                scheduleRepository.findByProjectWithMemberOrderByDates(project);

        // 이슈 수 일괄 조회 (N+1 방지)
        List<Long> scheduleIds = schedules.stream().map(ProjectSchedule::getId).collect(Collectors.toList());
        Map<Long, Integer> issueCounts = new HashMap<>();
        if (!scheduleIds.isEmpty()) {
            issueRepository.countByScheduleIds(scheduleIds, IssueStatus.DONE).forEach(row ->
                    issueCounts.put((Long) row[0], ((Long) row[1]).intValue()));
        }

        AtomicInteger counter = new AtomicInteger(1);
        return schedules.stream()
                .map(s -> new ProjectScheduleItemDto(s, counter.getAndIncrement(),
                        issueCounts.getOrDefault(s.getId(), 0)))
                .collect(Collectors.toList());
    }

    /** 이번 주(월~일) 기간과 겹치는 미완료 일정 목록 - 담당자 필터링 */
    public List<ProjectScheduleItemDto> findThisWeekSchedules(Long memberId) {
        LocalDate today     = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd   = today.with(DayOfWeek.SUNDAY);
        List<ProjectSchedule> list = scheduleRepository.findThisWeekSchedules(weekStart, weekEnd, memberId);
        AtomicInteger counter = new AtomicInteger(1);
        return list.stream()
                .map(s -> new ProjectScheduleItemDto(s, counter.getAndIncrement(), 0))
                .collect(Collectors.toList());
    }

    /** 종료일이 지났으나 완료되지 않은 지연 일정 목록 - 담당자 필터링 */
    public List<ProjectScheduleItemDto> findOverdueSchedules(Long memberId) {
        List<ProjectSchedule> list = scheduleRepository.findOverdueSchedules(LocalDate.now(), memberId);
        AtomicInteger counter = new AtomicInteger(1);
        return list.stream()
                .map(s -> new ProjectScheduleItemDto(s, counter.getAndIncrement(), 0))
                .collect(Collectors.toList());
    }

    @Transactional
    public void register(PmsRegisterRequest req) {
        log.info("[PMS register] projectId={}, phaseName={}, role={}, title={}, status={}",
                req.getProjectId(), req.getPhaseName(), req.getRole(), req.getTitle(), req.getStatus());

        if (req.getProjectId() == null) {
            throw new IllegalArgumentException("projectId가 누락되었습니다. 요청 데이터를 확인하세요.");
        }
        if (req.getPhaseName() == null || req.getPhaseName().isBlank()) {
            throw new IllegalArgumentException("차수명(phaseName)은 필수 항목입니다.");
        }
        if (req.getRole() == null || req.getRole().isBlank()) {
            throw new IllegalArgumentException("역할(role)은 필수 항목입니다.");
        }
        if (req.getTitle() == null || req.getTitle().isBlank()) {
            throw new IllegalArgumentException("업무명(title)은 필수 항목입니다.");
        }

        Project project = projectRepository.findById(req.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다. id=" + req.getProjectId()));

        ProjectMember projectMember = null;
        if (req.getProjectMemberId() != null) {
            projectMember = projectMemberRepository.findById(req.getProjectMemberId())
                    .orElseGet(() -> {
                        log.warn("[PMS register] projectMemberId={}인 멤버를 찾을 수 없어 미배정으로 처리합니다.", req.getProjectMemberId());
                        return null;
                    });
        }

        String dbStatus = req.getStatus() != null ? req.getStatus() : "PLANNED";

        ProjectSchedule schedule = new ProjectSchedule(
                project, projectMember,
                req.getRole(), req.getTitle(), null,
                req.getStartDate(), req.getEndDate(),
                dbStatus, req.getMemo(), req.getPhaseName()
        );
        scheduleRepository.save(schedule);
        log.info("[PMS register] 저장 완료. scheduleId={}", schedule.getId());

        // 새 항목 등록 시 프로젝트 상태를 IN_PROGRESS로 되돌림
        if (project.getStatus() == ProjectStatus.COMPLETED) {
            project.setStatus(ProjectStatus.IN_PROGRESS);
            log.info("[PMS register] 프로젝트 상태 IN_PROGRESS 전환 - projectId={}", project.getId());
        }
    }

    @Transactional
    public void complete(Long scheduleId) {
        // 1. 현재 일정 로드 (project, projectMember, member JOIN FETCH)
        ProjectSchedule current = scheduleRepository.findByIdWithMember(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found: " + scheduleId));

        // 2. DONE 처리
        current.setStatus("DONE");

        // 3. 해당 프로젝트의 전체 일정을 startDate → endDate 순으로 조회
        List<ProjectSchedule> all =
                scheduleRepository.findByProjectWithMemberOrderByDates(current.getProject());

        // 4. 현재 일정 다음 위치의 첫 번째 미완료 일정 탐색
        ProjectSchedule next = null;
        boolean foundCurrent = false;
        for (ProjectSchedule s : all) {
            if (s.getId().equals(scheduleId)) {
                foundCurrent = true;
                continue;
            }
            if (foundCurrent && !"DONE".equals(s.getStatus())) {
                next = s;
                break;
            }
        }

        // 5. 전체 완료 여부 확인 → 프로젝트 상태 자동 전환 (순서 무관)
        Project project = current.getProject();
        long total = scheduleRepository.countByProject(project);
        long done  = scheduleRepository.countByProjectAndStatus(project, "DONE");
        boolean allDone = total > 0 && total == done;
        if (allDone) {
            project.setStatus(ProjectStatus.COMPLETED);
            log.info("[PMS complete] 프로젝트 완료 처리 - projectId={}, name={}", project.getId(), project.getName());
        }

        // 6. Slack 알림 전송
        String currentName  = resolveAssigneeName(current);
        String currentPhase = current.getPhaseName() != null ? current.getPhaseName() : current.getTitle();

        if (next != null) {
            String nextName    = resolveAssigneeName(next);
            String nextPhase   = next.getPhaseName() != null ? next.getPhaseName() : next.getTitle();
            String nextSlackId = resolveSlackUserId(next);
            slackNotifierService.sendPmsComplete(currentName, currentPhase,
                    project.getName(), nextName, nextPhase, nextSlackId);
        } else if (allDone) {
            List<ProjectMember> allMembers = projectMemberRepository.findByProjectWithMember(project);
            slackNotifierService.sendPmsAllComplete(
                    project.getName(), currentName, currentPhase, allMembers);
        }

        log.info("[PMS complete] scheduleId={} DONE 처리, next={}", scheduleId,
                next != null ? next.getId() : "없음");
    }

    private String resolveAssigneeName(ProjectSchedule s) {
        if (s.getProjectMember() != null && s.getProjectMember().getMember() != null) {
            return s.getProjectMember().getMember().getName();
        }
        return "미배정";
    }

    private String resolveSlackUserId(ProjectSchedule s) {
        if (s.getProjectMember() != null && s.getProjectMember().getMember() != null) {
            return s.getProjectMember().getMember().getSlackUserId();
        }
        return null;
    }
}
