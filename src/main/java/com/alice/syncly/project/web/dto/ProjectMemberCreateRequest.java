package com.alice.syncly.project.web.dto;

import com.alice.syncly.project.domain.ProjectRole;

public class ProjectMemberCreateRequest {

    private Long projectId;
    private Long memberId;
    private ProjectRole projectRole;

    public ProjectMemberCreateRequest() {
    }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public ProjectRole getProjectRole() { return projectRole; }
    public void setProjectRole(ProjectRole projectRole) { this.projectRole = projectRole; }
}
