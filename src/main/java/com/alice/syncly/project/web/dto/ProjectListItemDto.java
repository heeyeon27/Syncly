package com.alice.syncly.project.web.dto;

import com.alice.syncly.project.domain.Project;
import com.alice.syncly.project.domain.ProjectStatus;

public class ProjectListItemDto {

    private final Long id;
    private final String name;
    private final String description;
    private final String statusLabel;   // "active" | "done"  (필터용 data-status)
    private final String statusBadge;   // "진행 중" | "완료"
    private final String ownerName;
    private final int memberCount;
    private final String endDateLabel;  // "yyyy-MM-dd" | "미정"
    private final int progressRate;     // 0–100
    private final boolean joined;

    public ProjectListItemDto(Project project, String ownerName, int memberCount, int progressRate, boolean joined) {
        this.id = project.getId();
        this.name = project.getName();
        this.description = project.getDescription();
        this.statusLabel = project.getStatus() == ProjectStatus.COMPLETED ? "done" : "active";
        this.statusBadge = project.getStatus() == ProjectStatus.COMPLETED ? "완료" : "진행 중";
        this.ownerName = ownerName;
        this.memberCount = memberCount;
        this.endDateLabel = project.getEndDate() != null ? project.getEndDate().toString() : "미정";
        this.progressRate = progressRate;
        this.joined = joined;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getStatusLabel() { return statusLabel; }
    public String getStatusBadge() { return statusBadge; }
    public String getOwnerName() { return ownerName; }
    public int getMemberCount() { return memberCount; }
    public String getEndDateLabel() { return endDateLabel; }
    public int getProgressRate() { return progressRate; }
    public boolean isJoined() { return joined; }
}
