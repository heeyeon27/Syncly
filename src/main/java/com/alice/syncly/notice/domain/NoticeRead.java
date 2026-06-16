package com.alice.syncly.notice.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notice_read")
public class NoticeRead {

    @EmbeddedId
    private NoticeReadId id;

    @Column(name = "read_at", nullable = false)
    private LocalDateTime readAt;

    protected NoticeRead() {}

    public NoticeRead(Long noticeId, Long memberId) {
        this.id = new NoticeReadId(noticeId, memberId);
    }

    @PrePersist
    protected void onCreate() {
        this.readAt = LocalDateTime.now();
    }

    public NoticeReadId getId() { return id; }
    public LocalDateTime getReadAt() { return readAt; }
}
