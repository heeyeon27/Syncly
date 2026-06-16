package com.alice.syncly.issue.web.dto;

import com.alice.syncly.issue.domain.Issue;

import java.util.Map;

public class IssueItemDto {

    private static final Map<String, String> PRIORITY_LABEL = Map.of(
            "URGENT", "긴급", "HIGH", "높음", "MEDIUM", "보통", "LOW", "낮음"
    );
    private static final Map<String, String> PRIORITY_CLASS = Map.of(
            "URGENT", "urgent", "HIGH", "high", "MEDIUM", "medium", "LOW", "low"
    );
    private static final Map<String, String> STATUS_LABEL = Map.of(
            "TODO", "대기", "IN_PROGRESS", "진행중", "DONE", "완료", "HOLD", "보류"
    );
    private static final Map<String, String> STATUS_CLASS = Map.of(
            "TODO", "isb-todo", "IN_PROGRESS", "isb-inprogress", "DONE", "isb-done", "HOLD", "isb-hold"
    );

    private final Long id;
    private final String title;
    private final String description;
    private final Long projectId;
    private final String priorityLabel;
    private final String priorityClass;
    private final String statusRaw;
    private final String statusLabel;
    private final String statusClass;
    private final String assigneeName;
    private final Long assigneeMemberId;
    private final String dueDateLabel;

    public IssueItemDto(Issue issue) {
        this.id            = issue.getId();
        this.title         = issue.getTitle();
        this.description   = issue.getDescription();
        this.projectId     = issue.getProject().getId();
        String p           = issue.getPriority().name();
        this.priorityLabel = PRIORITY_LABEL.getOrDefault(p, p);
        this.priorityClass = PRIORITY_CLASS.getOrDefault(p, "");
        String s           = issue.getStatus().name();
        this.statusRaw     = s;
        this.statusLabel   = STATUS_LABEL.getOrDefault(s, s);
        this.statusClass   = STATUS_CLASS.getOrDefault(s, "");
        this.assigneeName      = issue.getAssignee() != null ? issue.getAssignee().getName() : "-";
        this.assigneeMemberId  = issue.getAssignee() != null ? issue.getAssignee().getId() : null;
        this.dueDateLabel      = issue.getDueDate() != null ? issue.getDueDate().toString() : "-";
    }

    public Long getId()               { return id; }
    public String getTitle()          { return title; }
    public String getDescription()    { return description; }
    public Long getProjectId()        { return projectId; }
    public String getPriorityLabel()  { return priorityLabel; }
    public String getPriorityClass()  { return priorityClass; }
    public String getStatusRaw()      { return statusRaw; }
    public String getStatusLabel()    { return statusLabel; }
    public String getStatusClass()    { return statusClass; }
    public String getAssigneeName()   { return assigneeName; }
    public Long getAssigneeMemberId() { return assigneeMemberId; }
    public String getDueDateLabel()   { return dueDateLabel; }
}
