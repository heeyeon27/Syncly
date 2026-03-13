package com.alice.syncly.slack.repository;

import com.alice.syncly.slack.domain.SlackNotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlackNotificationLogRepository extends JpaRepository<SlackNotificationLog, Long> {
}
