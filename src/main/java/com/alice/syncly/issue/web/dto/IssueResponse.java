package com.alice.syncly.issue.web.dto;

import com.alice.syncly.issue.domain.Issue;

public class IssueResponse {

    private final Long id;
    private final String title;
    private final String priority;
    private final String status;
    private final String assigneeName;
    private final Long assigneeMemberId;
    private final String dueDateLabel;

    public IssueResponse(Issue issue) {
        this.id               = issue.getId();
        this.title            = issue.getTitle();
        this.priority         = issue.getPriority().name();
        this.status           = issue.getStatus().name();
        this.assigneeName     = issue.getAssignee() != null ? issue.getAssignee().getName() : null;
        this.assigneeMemberId = issue.getAssignee() != null ? issue.getAssignee().getId() : null;
        this.dueDateLabel     = issue.getDueDate() != null ? issue.getDueDate().toString() : null;
    }

    public Long getId()               { return id; }
    public String getTitle()          { return title; }
    public String getPriority()       { return priority; }
    public String getStatus()         { return status; }
    public String getAssigneeName()   { return assigneeName; }
    public Long getAssigneeMemberId() { return assigneeMemberId; }
    public String getDueDateLabel()   { return dueDateLabel; }
}
