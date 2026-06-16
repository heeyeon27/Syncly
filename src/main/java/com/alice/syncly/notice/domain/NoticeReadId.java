package com.alice.syncly.notice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class NoticeReadId implements Serializable {

    @Column(name = "notice_id")
    private Long noticeId;

    @Column(name = "member_id")
    private Long memberId;

    protected NoticeReadId() {}

    public NoticeReadId(Long noticeId, Long memberId) {
        this.noticeId = noticeId;
        this.memberId = memberId;
    }

    public Long getNoticeId() { return noticeId; }
    public Long getMemberId() { return memberId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NoticeReadId)) return false;
        NoticeReadId that = (NoticeReadId) o;
        return Objects.equals(noticeId, that.noticeId) && Objects.equals(memberId, that.memberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(noticeId, memberId);
    }
}
