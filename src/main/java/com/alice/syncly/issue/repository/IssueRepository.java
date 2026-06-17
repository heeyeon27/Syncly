package com.alice.syncly.issue.repository;

import com.alice.syncly.issue.domain.Issue;
import com.alice.syncly.issue.domain.IssuePriority;
import com.alice.syncly.issue.domain.IssueStatus;
import com.alice.syncly.project.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface IssueRepository extends JpaRepository<Issue, Long> {

    @Query("SELECT i FROM Issue i LEFT JOIN FETCH i.assignee LEFT JOIN FETCH i.reporter LEFT JOIN FETCH i.schedule WHERE i.project.id = :projectId AND i.deletedAt IS NULL")
    List<Issue> findByProjectIdWithMembers(@Param("projectId") Long projectId);

    @Query("SELECT i FROM Issue i JOIN FETCH i.project LEFT JOIN FETCH i.assignee LEFT JOIN FETCH i.reporter WHERE i.assignee.id = :memberId AND i.status <> :status AND i.deletedAt IS NULL")
    List<Issue> findByAssigneeIdExcludingStatus(@Param("memberId") Long memberId, @Param("status") IssueStatus status);

    @Query("SELECT i.schedule.id, COUNT(i) FROM Issue i WHERE i.schedule IS NOT NULL AND i.schedule.id IN :scheduleIds AND i.status <> :excludeStatus GROUP BY i.schedule.id")
    List<Object[]> countByScheduleIds(@Param("scheduleIds") List<Long> scheduleIds, @Param("excludeStatus") IssueStatus excludeStatus);

    @Query("SELECT COUNT(i) FROM Issue i WHERE i.assignee.id = :memberId AND i.priority = :priority AND i.status <> :excludeStatus")
    long countByAssigneeAndPriorityExcludingStatus(@Param("memberId") Long memberId, @Param("priority") IssuePriority priority, @Param("excludeStatus") IssueStatus excludeStatus);

    List<Issue> findByProjectOrderByPriorityAsc(Project project);

    List<Issue> findByProjectAndStatus(Project project, IssueStatus status);

    List<Issue> findByProjectAndPriority(Project project, IssuePriority priority);

    long countByStatus(IssueStatus status);

    long countByPriority(IssuePriority priority);
}
