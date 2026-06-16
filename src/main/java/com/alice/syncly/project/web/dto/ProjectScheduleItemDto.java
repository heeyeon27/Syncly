package com.alice.syncly.project.web.dto;

import com.alice.syncly.project.domain.ProjectMember;
import com.alice.syncly.project.domain.ProjectSchedule;

import java.util.Map;

public class ProjectScheduleItemDto {

    private static final Map<String, String> ROLE_CLASS_MAP = Map.of(
            "PM",        "role-pm",
            "DESIGN",    "role-design",
            "PUBLISHER", "role-publish",
            "DEVELOPER", "role-backend",
            "PLANNER",   "role-pm",
            "QA",        "role-qa"
    );

    private static final Map<String, String> ROLE_LABEL_MAP = Map.of(
            "PM",        "PM",
            "DESIGN",    "디자인",
            "PUBLISHER", "퍼블",
            "DEVELOPER", "개발",
            "PLANNER",   "기획",
            "QA",        "QA"
    );

    private static final Map<String, String> STATUS_CLASS_MAP = Map.of(
            "PLANNED",     "pending",
            "IN_PROGRESS", "active",
            "DELAYED",     "warn",
            "DONE",        "done",
            "CANCELLED",   "done"
    );

    private static final Map<String, String> STATUS_LABEL_MAP = Map.of(
            "PLANNED",     "대기",
            "IN_PROGRESS", "진행중",
            "DELAYED",     "검토중",
            "DONE",        "완료",
            "CANCELLED",   "취소"
    );

    private final Long id;
    private final int rowNum;
    private final Long projectId;
    private final String projectName;
    private final String role;
    private final String roleClass;
    private final String assigneeInitial;
    private final String assigneeName;
    private final Long assigneeMemberId;
    private final String phaseName;
    private final String title;
    private final String startDateLabel;
    private final String endDateLabel;
    private final String statusClass;
    private final String statusLabel;
    private final String memo;
    private final boolean done;
    private final int issueCount;

    public ProjectScheduleItemDto(ProjectSchedule s, int rowNum, int issueCount) {
        this.id = s.getId();
        this.rowNum = rowNum;
        this.projectId = s.getProject() != null ? s.getProject().getId() : null;
        this.projectName = s.getProject() != null ? s.getProject().getName() : "-";
        this.issueCount = issueCount;
        String rawRole = s.getRoleType() != null ? s.getRoleType() : "-";
        this.role      = ROLE_LABEL_MAP.getOrDefault(rawRole, rawRole);
        this.roleClass = ROLE_CLASS_MAP.getOrDefault(rawRole, "role-etc");

        ProjectMember pm = s.getProjectMember();
        boolean hasAssignee = pm != null && pm.getMember() != null;
        this.assigneeName     = hasAssignee ? pm.getMember().getName() : "미배정";
        this.assigneeInitial  = hasAssignee ? pm.getMember().getName().substring(0, 1) : "-";
        this.assigneeMemberId = hasAssignee ? pm.getMember().getId() : null;

        this.phaseName      = s.getPhaseName() != null ? s.getPhaseName() : "-";
        this.title          = s.getTitle();
        this.startDateLabel = s.getStartDate() != null ? s.getStartDate().toString() : "-";
        this.endDateLabel   = s.getEndDate() != null ? s.getEndDate().toString() : "-";

        String st = s.getStatus() != null ? s.getStatus() : "TODO";
        this.statusClass = STATUS_CLASS_MAP.getOrDefault(st, "pending");
        this.statusLabel = STATUS_LABEL_MAP.getOrDefault(st, "대기");
        this.done = "DONE".equals(st);

        this.memo = s.getMemo() != null ? s.getMemo() : "-";
    }

    public Long getId()             { return id; }
    public int getRowNum()          { return rowNum; }
    public Long getProjectId()      { return projectId; }
    public String getProjectName()  { return projectName; }
    public String getRole()         { return role; }
    public String getRoleClass()    { return roleClass; }
    public String getAssigneeInitial() { return assigneeInitial; }
    public String getAssigneeName() { return assigneeName; }
    public Long getAssigneeMemberId() { return assigneeMemberId; }
    public String getPhaseName()    { return phaseName; }
    public String getTitle()        { return title; }
    public String getStartDateLabel() { return startDateLabel; }
    public String getEndDateLabel() { return endDateLabel; }
    public String getStatusClass()  { return statusClass; }
    public String getStatusLabel()  { return statusLabel; }
    public String getMemo()         { return memo; }
    public boolean isDone()         { return done; }
    public int getIssueCount()      { return issueCount; }
}
