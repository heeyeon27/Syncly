package com.alice.syncly.project.domain;

import com.alice.syncly.member.domain.Member;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Member owner;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Project() {
    }

    public Project(String name, String description, Member owner) {
        this.name = name;
        this.description = description;
        this.owner = owner;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Member getOwner() { return owner; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
