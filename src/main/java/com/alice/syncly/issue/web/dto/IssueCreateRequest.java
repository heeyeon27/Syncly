package com.alice.syncly.issue.web.dto;

import java.time.LocalDate;

public class IssueCreateRequest {

    private Long projectId;
    private String title;
    private String description;
    private String priority;
    private Long assigneeId;
    private LocalDate dueDate;
    private Long scheduleId;

    public Long getProjectId()    { return projectId; }
    public String getTitle()      { return title; }
    public String getDescription(){ return description; }
    public String getPriority()   { return priority; }
    public Long getAssigneeId()   { return assigneeId; }
    public LocalDate getDueDate() { return dueDate; }
    public Long getScheduleId()   { return scheduleId; }
}
