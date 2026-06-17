package com.alice.syncly.project.web.dto;

import com.alice.syncly.project.domain.Project;
import com.alice.syncly.project.domain.ProjectStatus;

public class ProjectDetailDto {

    private final Long id;
    private final String name;
    private final String description;
    private final String statusBadgeClass; // "green" | "blue"
    private final String statusLabel;
    private final String ownerName;
    private final Long ownerMemberId;
    private final String startDateLabel;
    private final String endDateLabel;
    private final int memberCount;
    private final int progressRate;

    public ProjectDetailDto(Project project, int memberCount, int progressRate) {
        this.id = project.getId();
        this.name = project.getName();
        this.description = project.getDescription();
        boolean completed = project.getStatus() == ProjectStatus.COMPLETED;
        this.statusBadgeClass = completed ? "blue" : "green";
        this.statusLabel = completed ? "완료" : "진행중";
        this.ownerName = project.getOwner().getName();
        this.ownerMemberId = project.getOwner().getId();
        this.startDateLabel = project.getStartDate() != null ? project.getStartDate().toString() : "미정";
        this.endDateLabel = project.getEndDate() != null ? project.getEndDate().toString() : "미정";
        this.memberCount = memberCount;
        this.progressRate = progressRate;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getStatusBadgeClass() { return statusBadgeClass; }
    public String getStatusLabel() { return statusLabel; }
    public String getOwnerName() { return ownerName; }
    public Long getOwnerMemberId() { return ownerMemberId; }
    public String getStartDateLabel() { return startDateLabel; }
    public String getEndDateLabel() { return endDateLabel; }
    public int getMemberCount() { return memberCount; }
    public int getProgressRate() { return progressRate; }
}
