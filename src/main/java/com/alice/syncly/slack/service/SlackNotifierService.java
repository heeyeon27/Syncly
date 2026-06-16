package com.alice.syncly.slack.service;

import com.alice.syncly.member.domain.Member;
import com.alice.syncly.project.domain.ProjectMember;
import com.alice.syncly.slack.domain.SlackNotificationLog;
import com.alice.syncly.slack.repository.SlackNotificationLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
public class SlackNotifierService {

    private static final Logger log = LoggerFactory.getLogger(SlackNotifierService.class);

    @Value("${slack.webhook.url}")
    private String webhookUrl;

    @Value("${slack.bot.token}")
    private String botToken;

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
        log.info("[SIGNUP] 회원가입 알림 | 이름: {} | 이메일: {} | 결과: {}", name, email, sendStatus);
        logRepository.save(new SlackNotificationLog("SIGNUP", memberId, messageText, sendStatus));
    }

    /** 프로젝트 초대 알림 (비동기 — 초대자 요약 DM 1건 + 피초대자 개별 DM) */
    @Async
    public void sendProjectInvitations(String inviterName, String inviterSlackUserId,
                                       String projectName, List<Member> invitees) {
        if (invitees.isEmpty()) return;

        // 피초대자 목록 문자열 (초대자 요약 메시지용)
        String inviteeListStr = invitees.stream()
                .map(m -> (m.getSlackUserId() != null && !m.getSlackUserId().isBlank())
                        ? "<@" + m.getSlackUserId() + ">"
                        : m.getName())
                .collect(Collectors.joining(", "));

        // 1. 초대자에게 요약 DM
        String inviterMsg = String.format("%s 님이 *%s* 프로젝트에 %s 님을 초대했습니다.",
                inviterName, projectName, inviteeListStr);
        boolean hasInviterSlack = inviterSlackUserId != null && !inviterSlackUserId.isBlank();
        log.info("[INVITE] 초대자 DM | 이름: {} | slack_user_id: {}",
                inviterName, hasInviterSlack ? inviterSlackUserId : "(없음)");
        if (hasInviterSlack) {
            sendDm(inviterSlackUserId, inviterMsg, inviterName);
        }

        // 2. 피초대자 각각에게 개별 DM
        for (Member invitee : invitees) {
            String slackId = invitee.getSlackUserId();
            boolean hasId  = slackId != null && !slackId.isBlank();
            String inviteeRef = hasId ? "<@" + slackId + ">" : invitee.getName();
            String inviteeMsg = String.format("%s 님이 *%s* 프로젝트에 %s 님을 초대했습니다.",
                    inviterName, projectName, inviteeRef);

            log.info("[INVITE] 피초대자 DM | 이름: {} | slack_user_id: {}",
                    invitee.getName(), hasId ? slackId : "(없음)");
            if (hasId) {
                sendDm(slackId, inviteeMsg, invitee.getName());
            } else {
                log.warn("[INVITE] slack_user_id 없음 - DM 건너뜀: {}", invitee.getName());
            }
        }
    }

    public void sendScheduleEnd(String taskTitle, String projectName) {
        String text = String.format("*%s* 프로젝트의 일정 *%s* 이(가) 종료되었습니다.", projectName, taskTitle);
        boolean success = send(text);
        log.info("[SCHEDULE] 일정 종료 알림 | 프로젝트: {} | 일정: {} | 결과: {}",
                projectName, taskTitle, success ? "SUCCESS" : "FAIL");
    }

    /** PMS 일정 완료 — 다음 담당자에게 개별 DM */
    @Async
    public void sendPmsComplete(String currentName, String currentPhase, String projectName,
                                String nextName, String nextPhase, String nextSlackUserId) {
        boolean hasId = nextSlackUserId != null && !nextSlackUserId.isBlank();
        String nextRef = hasId ? "<@" + nextSlackUserId + ">" : "@" + nextName;
        String text = String.format(
                "*%s* 님의 [*%s*] '%s' 작업이 완료되었습니다.\n다음 담당자 %s 님, '%s' 작업을 시작해주세요.",
                currentName, projectName, currentPhase, nextRef, nextPhase);

        log.info("[PMS] 단계 완료 알림 | 완료자: {} | 프로젝트: {} | 다음담당자: {} | slack_user_id: {}",
                currentName, projectName, nextName, hasId ? nextSlackUserId : "(없음)");

        if (hasId) {
            sendDm(nextSlackUserId, text, nextName);
        } else {
            log.warn("[PMS] slack_user_id 없음 - DM 건너뜀: {}", nextName);
        }
    }

    /** 이슈 담당자 알림 (비동기 — 담당자에게 개별 DM) */
    @Async
    public void sendIssueAssigned(String projectName, String title,
                                  String assigneeName, String assigneeSlackUserId) {
        boolean hasId = assigneeSlackUserId != null && !assigneeSlackUserId.isBlank();
        log.info("[ISSUE] 이슈 할당 알림 | 프로젝트: {} | 제목: {} | 담당자: {} | slack_user_id: {}",
                projectName, title, assigneeName, hasId ? assigneeSlackUserId : "(없음)");

        if (!hasId) {
            log.warn("[ISSUE] slack_user_id 없음 - DM 건너뜀: {}", assigneeName);
            return;
        }
        String text = String.format("[*%s*] *%s* 이슈가 %s 님께 할당되었습니다. 확인해주세요.",
                projectName, title, assigneeName);
        sendDm(assigneeSlackUserId, text, assigneeName);
    }

    /** 공지사항 등록 알림 (비동기 — 개별 DM 전송) */
    @Async
    public void sendNoticeCreated(String projectName, String noticeTitle, String authorName,
                                  List<ProjectMember> members) {
        log.info("[Notice] Slack DM 발송 시작 - 프로젝트: {}, 작성자: {}, 대상: {}명",
                projectName, authorName, members.size());

        String text = String.format("%s 님이 *%s* 프로젝트에 새 공지 [ *%s* ]를 등록했습니다.",
                authorName, projectName, noticeTitle);

        for (ProjectMember pm : members) {
            String name    = pm.getMember().getName();
            String slackId = pm.getMember().getSlackUserId();
            boolean hasId  = slackId != null && !slackId.isBlank();

            log.info("[Notice] 발송 대상 | 이름: {} | slack_user_id: {}", name, hasId ? slackId : "(없음)");

            if (!hasId) {
                log.warn("[Notice] slack_user_id 없음 - DM 건너뜀: {}", name);
                continue;
            }

            sendDm(slackId, text, name);
        }
    }

    private void sendDm(String slackUserId, String text, String memberName) {
        try {
            // 1. DM 채널 열기 (conversations.open)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(botToken);

            Map<String, Object> openBody = new HashMap<>();
            openBody.put("users", slackUserId);
            HttpEntity<Map<String, Object>> openReq = new HttpEntity<>(openBody, headers);

            ResponseEntity<Map> openRes = restTemplate.postForEntity(
                    "https://slack.com/api/conversations.open", openReq, Map.class);

            Map<?, ?> openResBody = openRes.getBody();
            log.info("[Notice] conversations.open 응답 - 대상: {} | status: {} | body: {}",
                    memberName, openRes.getStatusCode(), openResBody);

            if (openResBody == null || !Boolean.TRUE.equals(openResBody.get("ok"))) {
                log.warn("[Notice] conversations.open 실패 - 대상: {} | error: {}",
                        memberName, openResBody != null ? openResBody.get("error") : "null response");
                return;
            }

            Map<?, ?> channel = (Map<?, ?>) openResBody.get("channel");
            String channelId  = (String) channel.get("id");

            // 2. DM 메시지 전송 (chat.postMessage)
            Map<String, Object> msgBody = new HashMap<>();
            msgBody.put("channel", channelId);
            msgBody.put("text", text);
            HttpEntity<Map<String, Object>> msgReq = new HttpEntity<>(msgBody, headers);

            ResponseEntity<Map> msgRes = restTemplate.postForEntity(
                    "https://slack.com/api/chat.postMessage", msgReq, Map.class);

            Map<?, ?> msgResBody = msgRes.getBody();
            log.info("[Notice] chat.postMessage 응답 - 대상: {} | status: {} | ok: {}",
                    memberName, msgRes.getStatusCode(),
                    msgResBody != null ? msgResBody.get("ok") : "null");

            if (msgResBody != null && !Boolean.TRUE.equals(msgResBody.get("ok"))) {
                log.warn("[Notice] chat.postMessage 실패 - 대상: {} | error: {}",
                        memberName, msgResBody.get("error"));
            }

        } catch (Exception e) {
            log.warn("[Notice] DM 전송 중 예외 - 대상: {} | error: {}", memberName, e.getMessage());
        }
    }

    /** PMS 모든 일정 완료 알림 (비동기 — 전체 멤버 개별 DM) */
    @Async
    public void sendPmsAllComplete(String projectName, String currentName, String currentPhase,
                                   List<ProjectMember> members) {
        String text = String.format(
                "*%s* 님의 [*%s*] '%s' 작업이 완료되었습니다.\n*%s* 프로젝트의 모든 일정이 완료되었습니다.",
                currentName, projectName, currentPhase, projectName);

        log.info("[PMS] 전체 완료 알림 | 프로젝트: {} | 최종완료자: {} | 대상: {}명",
                projectName, currentName, members.size());

        for (ProjectMember pm : members) {
            String name    = pm.getMember().getName();
            String slackId = pm.getMember().getSlackUserId();
            boolean hasId  = slackId != null && !slackId.isBlank();

            log.info("[PMS] 전체완료 발송 대상 | 이름: {} | slack_user_id: {}", name, hasId ? slackId : "(없음)");
            if (hasId) {
                sendDm(slackId, text, name);
            } else {
                log.warn("[PMS] slack_user_id 없음 - DM 건너뜀: {}", name);
            }
        }
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
