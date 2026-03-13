package com.alice.syncly.slack.domain;

import com.alice.syncly.project.domain.Project;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "slack_integration")
public class SlackIntegration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "channel_name")
    private String channelName;

    @Column(name = "webhook_url")
    private String webhookUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SlackIntegrationStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected SlackIntegration() {
    }

    public SlackIntegration(Project project, String channelName, String webhookUrl) {
        this.project = project;
        this.channelName = channelName;
        this.webhookUrl = webhookUrl;
        this.status = SlackIntegrationStatus.DISCONNECTED;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Project getProject() { return project; }
    public String getChannelName() { return channelName; }
    public String getWebhookUrl() { return webhookUrl; }
    public SlackIntegrationStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setStatus(SlackIntegrationStatus status) { this.status = status; }
    public void setChannelName(String channelName) { this.channelName = channelName; }
    public void setWebhookUrl(String webhookUrl) { this.webhookUrl = webhookUrl; }
}
