package com.alice.syncly.issue.repository;

import com.alice.syncly.issue.domain.Issue;
import com.alice.syncly.issue.domain.IssueComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueCommentRepository extends JpaRepository<IssueComment, Long> {

    List<IssueComment> findByIssueOrderByCreatedAtAsc(Issue issue);
}
