package com.alice.syncly.project.repository;

import com.alice.syncly.member.domain.Member;
import com.alice.syncly.project.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByOwner(Member owner);

    List<Project> findByNameContainingIgnoreCase(String name);
}
