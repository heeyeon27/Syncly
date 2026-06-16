package com.alice.syncly.notice.domain;

import com.alice.syncly.member.domain.Member;
import com.alice.syncly.project.domain.Project;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notice")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Member author;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Notice() {
    }

    public Notice(String title, String content, Project project, Member author) {
        this.title = title;
        this.content = content;
        this.project = project;
        this.author = author;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Project getProject() { return project; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public Member getAuthor() { return author; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
