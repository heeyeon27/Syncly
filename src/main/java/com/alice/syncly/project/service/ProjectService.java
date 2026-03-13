package com.alice.syncly.project.service;

import com.alice.syncly.member.domain.Member;
import com.alice.syncly.member.repository.MemberRepository;
import com.alice.syncly.project.domain.Project;
import com.alice.syncly.project.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;

    public ProjectService(ProjectRepository projectRepository, MemberRepository memberRepository) {
        this.projectRepository = projectRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Project createProject(String name, String description, Long ownerId) {
        Member owner = memberRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Owner not found: " + ownerId));
        Project project = new Project(name, description, owner);
        return projectRepository.save(project);
    }

    public Optional<Project> findById(Long id) {
        return projectRepository.findById(id);
    }

    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    public List<Project> searchByName(String name) {
        return projectRepository.findByNameContainingIgnoreCase(name);
    }
}
