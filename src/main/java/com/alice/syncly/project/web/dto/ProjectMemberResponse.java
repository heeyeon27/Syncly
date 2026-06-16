package com.alice.syncly.project.web.dto;

import com.alice.syncly.project.domain.ProjectMember;

public class ProjectMemberResponse {

    private final Long id;
    private final Long memberId;
    private final String memberName;
    private final String memberEmail;
    private final String projectRole;

    public ProjectMemberResponse(ProjectMember pm) {
        this.id          = pm.getId();
        this.memberId    = pm.getMember().getId();
        this.memberName  = pm.getMember().getName();
        this.memberEmail = pm.getMember().getEmail();
        this.projectRole = pm.getProjectRole().name();
    }

    public Long getId()          { return id; }
    public Long getMemberId()    { return memberId; }
    public String getMemberName()  { return memberName; }
    public String getMemberEmail() { return memberEmail; }
    public String getProjectRole() { return projectRole; }
}
