package com.alice.syncly.issue.domain;

import com.alice.syncly.member.domain.Member;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "issue_comment")
public class IssueComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id", nullable = false)
    private Issue issue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Member author;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected IssueComment() {
    }

    public IssueComment(Issue issue, Member author, String content) {
        this.issue = issue;
        this.author = author;
        this.content = content;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Issue getIssue() { return issue; }
    public Member getAuthor() { return author; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
