package com.alice.syncly.project.repository;

import com.alice.syncly.project.domain.Project;
import com.alice.syncly.project.domain.ProjectSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProjectScheduleRepository extends JpaRepository<ProjectSchedule, Long> {

    long countByProject(Project project);

    long countByProjectAndStatus(Project project, String status);

    @Query("SELECT s FROM ProjectSchedule s " +
           "LEFT JOIN FETCH s.projectMember pm " +
           "LEFT JOIN FETCH pm.member " +
           "WHERE s.project = :project AND s.deletedAt IS NULL " +
           "ORDER BY s.startDate ASC, s.endDate ASC")
    List<ProjectSchedule> findByProjectWithMemberOrderByDates(@Param("project") Project project);

    @Query("SELECT s FROM ProjectSchedule s " +
           "JOIN FETCH s.project " +
           "LEFT JOIN FETCH s.projectMember pm " +
           "LEFT JOIN FETCH pm.member " +
           "WHERE s.id = :id")
    Optional<ProjectSchedule> findByIdWithMember(@Param("id") Long id);

    /** 이번 주(weekStart~weekEnd) 기간과 겹치는 미완료 일정 - 담당자 필터링 */
    @Query("SELECT s FROM ProjectSchedule s " +
           "JOIN FETCH s.project " +
           "JOIN FETCH s.projectMember pm " +
           "JOIN FETCH pm.member m " +
           "WHERE s.status <> 'DONE' AND s.deletedAt IS NULL " +
           "AND s.startDate <= :weekEnd AND s.endDate >= :weekStart " +
           "AND m.id = :memberId " +
           "ORDER BY s.endDate ASC")
    List<ProjectSchedule> findThisWeekSchedules(@Param("weekStart") LocalDate weekStart,
                                                @Param("weekEnd") LocalDate weekEnd,
                                                @Param("memberId") Long memberId);

    /** 종료일이 지났으나 완료되지 않은 지연 일정 - 담당자 필터링 */
    @Query("SELECT s FROM ProjectSchedule s " +
           "JOIN FETCH s.project " +
           "JOIN FETCH s.projectMember pm " +
           "JOIN FETCH pm.member m " +
           "WHERE s.status <> 'DONE' AND s.deletedAt IS NULL AND s.endDate < :today " +
           "AND m.id = :memberId " +
           "ORDER BY s.endDate ASC")
    List<ProjectSchedule> findOverdueSchedules(@Param("today") LocalDate today,
                                               @Param("memberId") Long memberId);
}
