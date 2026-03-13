package com.alice.syncly.member.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "system_role", nullable = false)
    private Role systemRole;

    @Column(name = "slack_user_id", nullable = false)
    private String slackUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus approvalStatus;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime deletedAt;

    protected Member() {
    }

    public Member(String email, String password, String name, Role systemRole, String slackUserId) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.systemRole = systemRole;
        this.slackUserId = slackUserId;
        this.approvalStatus = ApprovalStatus.PENDING;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public Role getSystemRole() { return systemRole; }
    public String getSlackUserId() { return slackUserId; }
    public ApprovalStatus getApprovalStatus() { return approvalStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getDeletedAt() { return deletedAt; }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }
}
