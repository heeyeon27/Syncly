package com.alice.syncly.project.web.dto;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class ProjectCreateRequest {

    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Long> invitedMemberIds;

    public ProjectCreateRequest() {
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public List<Long> getInvitedMemberIds() {
        return invitedMemberIds != null ? invitedMemberIds : Collections.emptyList();
    }
    public void setInvitedMemberIds(List<Long> invitedMemberIds) { this.invitedMemberIds = invitedMemberIds; }
}
