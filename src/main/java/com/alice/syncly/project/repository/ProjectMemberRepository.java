package com.alice.syncly.project.repository;

import com.alice.syncly.member.domain.Member;
import com.alice.syncly.project.domain.Project;
import com.alice.syncly.project.domain.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    List<ProjectMember> findByProject(Project project);

    List<ProjectMember> findByMember(Member member);

    @Query("SELECT CASE WHEN COUNT(pm) > 0 THEN true ELSE false END FROM ProjectMember pm WHERE pm.project.id = :projectId AND pm.member.id = :memberId AND pm.deletedAt IS NULL")
    boolean existsByProjectIdAndMemberId(@Param("projectId") Long projectId, @Param("memberId") Long memberId);

    @Query("SELECT pm FROM ProjectMember pm JOIN FETCH pm.member WHERE pm.project = :project AND pm.deletedAt IS NULL")
    List<ProjectMember> findByProjectWithMember(@Param("project") Project project);
}
