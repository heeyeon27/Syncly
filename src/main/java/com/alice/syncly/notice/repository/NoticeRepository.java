package com.alice.syncly.notice.repository;

import com.alice.syncly.notice.domain.Notice;
import com.alice.syncly.project.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    @Query("SELECT n FROM Notice n JOIN FETCH n.author WHERE n.project = :project ORDER BY n.createdAt DESC")
    List<Notice> findByProjectWithAuthorOrderByCreatedAtDesc(@Param("project") Project project);

    @Query("SELECT n FROM Notice n " +
           "JOIN FETCH n.author " +
           "JOIN FETCH n.project " +
           "JOIN ProjectMember pm ON pm.project = n.project " +
           "WHERE pm.member.id = :memberId " +
           "AND n.id NOT IN (" +
           "  SELECT r.id.noticeId FROM NoticeRead r WHERE r.id.memberId = :memberId" +
           ") " +
           "ORDER BY n.createdAt DESC")
    List<Notice> findUnreadByMember(@Param("memberId") Long memberId);
}
