package com.alice.syncly.project.repository;

import com.alice.syncly.project.domain.Project;
import com.alice.syncly.project.domain.ProjectSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectScheduleRepository extends JpaRepository<ProjectSchedule, Long> {

    long countByProject(Project project);

    long countByProjectAndStatus(Project project, String status);

    @Query("SELECT s FROM ProjectSchedule s " +
           "LEFT JOIN FETCH s.projectMember pm " +
           "LEFT JOIN FETCH pm.member " +
           "WHERE s.project = :project " +
           "ORDER BY s.startDate ASC, s.endDate ASC")
    List<ProjectSchedule> findByProjectWithMemberOrderByDates(@Param("project") Project project);

    @Query("SELECT s FROM ProjectSchedule s " +
           "JOIN FETCH s.project " +
           "LEFT JOIN FETCH s.projectMember pm " +
           "LEFT JOIN FETCH pm.member " +
           "WHERE s.id = :id")
    Optional<ProjectSchedule> findByIdWithMember(@Param("id") Long id);
}
