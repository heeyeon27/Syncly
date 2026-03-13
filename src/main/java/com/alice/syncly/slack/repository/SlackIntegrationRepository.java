package com.alice.syncly.slack.repository;

import com.alice.syncly.project.domain.Project;
import com.alice.syncly.slack.domain.SlackIntegration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SlackIntegrationRepository extends JpaRepository<SlackIntegration, Long> {

    Optional<SlackIntegration> findByProject(Project project);
}
