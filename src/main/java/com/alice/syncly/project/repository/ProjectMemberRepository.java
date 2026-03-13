package com.alice.syncly.project.repository;

import com.alice.syncly.member.domain.Member;
import com.alice.syncly.project.domain.Project;
import com.alice.syncly.project.domain.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    List<ProjectMember> findByProject(Project project);

    List<ProjectMember> findByMember(Member member);
}
