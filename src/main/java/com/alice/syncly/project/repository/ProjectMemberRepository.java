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

    boolean existsByProjectIdAndMemberId(Long projectId, Long memberId);

    @Query("SELECT pm FROM ProjectMember pm JOIN FETCH pm.member WHERE pm.project = :project")
    List<ProjectMember> findByProjectWithMember(@Param("project") Project project);
}
