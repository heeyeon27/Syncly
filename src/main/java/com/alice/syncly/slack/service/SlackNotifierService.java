package com.alice.syncly.slack.service;

import com.alice.syncly.slack.domain.SlackNotificationLog;
import com.alice.syncly.slack.repository.SlackNotificationLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class SlackNotifierService {

    private static final Logger log = LoggerFactory.getLogger(SlackNotifierService.class);

    @Value("${slack.webhook.url}")
    private String webhookUrl;

    private final RestTemplate restTemplate;
    private final SlackNotificationLogRepository logRepository;

    public SlackNotifierService(RestTemplate restTemplate,
                                SlackNotificationLogRepository logRepository) {
        this.restTemplate = restTemplate;
        this.logRepository = logRepository;
    }

    @Transactional
    public void sendSignupNotification(Long memberId, String name, String email) {
        String messageText = "회원가입이 요청되었습니다. 승인 후 사용이 가능합니다";
        String slackText = String.format(
                "회원가입이 요청되었습니다.\n승인 후 사용이 가능합니다.\n\n이름: %s\n이메일: %s",
                name, email);

        boolean success = send(slackText);
        String sendStatus = success ? "SUCCESS" : "FAIL";

        logRepository.save(new SlackNotificationLog("SIGNUP", memberId, messageText, sendStatus));
    }

    public void sendProjectInvitation(String memberName, String projectName) {
        String text = String.format("*%s* 님이 *%s* 프로젝트에 초대되었습니다.", memberName, projectName);
        send(text);
    }

    public void sendScheduleEnd(String taskTitle, String projectName) {
        String text = String.format("*%s* 프로젝트의 일정 *%s* 이(가) 종료되었습니다.", projectName, taskTitle);
        send(text);
    }

    private boolean send(String text) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, String> payload = Map.of("text", text);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(payload, headers);
            restTemplate.postForEntity(webhookUrl, entity, String.class);
            return true;
        } catch (Exception e) {
            log.warn("[SlackNotifier] 메시지 전송 실패: {}", e.getMessage());
            return false;
        }
    }
}
