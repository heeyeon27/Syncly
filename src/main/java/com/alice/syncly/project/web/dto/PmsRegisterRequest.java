package com.alice.syncly.project.web.dto;

import java.time.LocalDate;

public class PmsRegisterRequest {

    private Long projectId;
    private String phaseName;  // 차수명 직접 입력 → find-or-create
    private Long projectMemberId;  // nullable (미배정 허용)
    private String role;       // PM, 디자인, 퍼블, 프론트, 백엔드, QA, 기타
    private String title;      // 업무명
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;     // pending | active | warn | done  (UI값 → 서비스에서 DB값으로 변환)
    private String memo;

    public Long getProjectId()       { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getPhaseName()     { return phaseName; }
    public void setPhaseName(String phaseName) { this.phaseName = phaseName; }

    public Long getProjectMemberId()           { return projectMemberId; }
    public void setProjectMemberId(Long projectMemberId) { this.projectMemberId = projectMemberId; }

    public String getRole()          { return role; }
    public void setRole(String role) { this.role = role; }

    public String getTitle()         { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDate getStartDate()  { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate()    { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getStatus()        { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMemo()          { return memo; }
    public void setMemo(String memo) { this.memo = memo; }
}
