package com.alice.syncly.notice.repository;

import com.alice.syncly.notice.domain.NoticeRead;
import com.alice.syncly.notice.domain.NoticeReadId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeReadRepository extends JpaRepository<NoticeRead, NoticeReadId> {
}
