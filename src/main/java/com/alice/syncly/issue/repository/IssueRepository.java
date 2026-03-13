package com.alice.syncly.issue.repository;

import com.alice.syncly.issue.domain.Issue;
import com.alice.syncly.issue.domain.IssuePriority;
import com.alice.syncly.issue.domain.IssueStatus;
import com.alice.syncly.project.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {

    List<Issue> findByProjectOrderByPriorityAsc(Project project);

    List<Issue> findByProjectAndStatus(Project project, IssueStatus status);

    List<Issue> findByProjectAndPriority(Project project, IssuePriority priority);

    long countByStatus(IssueStatus status);

    long countByPriority(IssuePriority priority);
}
