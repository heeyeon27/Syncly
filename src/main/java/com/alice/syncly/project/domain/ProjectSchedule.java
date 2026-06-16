package com.alice.syncly.project.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "project_schedule")
public class ProjectSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_member_id")
    private ProjectMember projectMember; // nullable (미배정 허용)

    @Column(name = "role_type")
    private String roleType;

    @Column(nullable = false)
    private String title;

    @Column
    private String description;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(nullable = false)
    private String status; // TODO, IN_PROGRESS, REVIEW, DONE

    @Column
    private String memo;

    @Column(name = "phase_name")
    private String phaseName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    protected ProjectSchedule() {}

    public ProjectSchedule(Project project, ProjectMember projectMember,
                           String roleType, String title, String description,
                           LocalDate startDate, LocalDate endDate,
                           String status, String memo, String phaseName) {
        this.project       = project;
        this.projectMember = projectMember;
        this.roleType      = roleType;
        this.title         = title;
        this.description   = description;
        this.startDate     = startDate;
        this.endDate       = endDate;
        this.status        = status;
        this.memo          = memo;
        this.phaseName     = phaseName;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId()                  { return id; }
    public Project getProject()          { return project; }
    public ProjectMember getProjectMember() { return projectMember; }
    public String getRoleType()          { return roleType; }
    public String getTitle()             { return title; }
    public String getDescription()       { return description; }
    public LocalDate getStartDate()      { return startDate; }
    public LocalDate getEndDate()        { return endDate; }
    public String getStatus()            { return status; }
    public String getMemo()              { return memo; }
    public String getPhaseName()         { return phaseName; }
    public LocalDateTime getCreatedAt()  { return createdAt; }
    public LocalDateTime getUpdatedAt()  { return updatedAt; }
    public LocalDateTime getDeletedAt()  { return deletedAt; }

    public void setStatus(String status) { this.status = status; }
}
