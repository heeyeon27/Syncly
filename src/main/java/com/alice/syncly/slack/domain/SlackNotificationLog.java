package com.alice.syncly.slack.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "slack_notification_log")
public class SlackNotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "issue_id")
    private Long issueId;

    @Column(name = "notice_id")
    private Long noticeId;

    @Column(name = "notification_type", nullable = false, length = 50)
    private String notificationType;

    @Column(name = "target_channel_id")
    private String targetChannelId;

    @Column(name = "target_slack_user_id")
    private String targetSlackUserId;

    @Column(name = "message_text", columnDefinition = "TEXT")
    private String messageText;

    @Column(name = "slack_message_ts")
    private String slackMessageTs;

    @Column(name = "send_status", nullable = false, length = 20)
    private String sendStatus;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    protected SlackNotificationLog() {}

    public SlackNotificationLog(String notificationType, Long memberId,
                                String messageText, String sendStatus) {
        this.notificationType = notificationType;
        this.memberId = memberId;
        this.messageText = messageText;
        this.sendStatus = sendStatus;
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
    public String getNotificationType() { return notificationType; }
    public Long getMemberId() { return memberId; }
    public String getMessageText() { return messageText; }
    public String getSendStatus() { return sendStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
