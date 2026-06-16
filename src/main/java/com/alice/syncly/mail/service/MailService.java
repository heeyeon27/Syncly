package com.alice.syncly.mail.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${slack.invite.url:}")
    private String slackInviteUrl;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendSlackInvite(String to) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

        helper.setTo(to);
        helper.setSubject("syncly-ops 슬랙 워크스페이스 초대");
        helper.setText(buildHtml(), true);

        mailSender.send(message);
    }

    private String buildHtml() {
        return """
            <div style="font-family:sans-serif; max-width:480px; margin:0 auto; padding:32px 24px; background:#f7f9fc; border-radius:12px;">
              <h2 style="color:#1a1a2e; margin-bottom:8px;">Syncly 팀에 오신 것을 환영합니다! 🎉</h2>
              <p style="color:#555; line-height:1.6;">
                아래 버튼을 클릭하면 <b>syncly-ops</b> 슬랙 워크스페이스에 바로 참여할 수 있습니다.
              </p>
              <div style="text-align:center; margin:32px 0;">
                <a href="%s"
                   style="display:inline-block; padding:14px 32px; background:#4A154B; color:#ffffff;
                          text-decoration:none; border-radius:8px; font-size:15px; font-weight:bold; letter-spacing:0.3px;">
                  슬랙 워크스페이스 참여하기
                </a>
              </div>
              <p style="color:#999; font-size:12px; margin-top:24px;">
                이 메일은 Syncly 회원가입 페이지에서 요청하셨습니다.<br>
                요청하지 않으셨다면 무시해 주세요.
              </p>
            </div>
            """.formatted(slackInviteUrl);
    }
}
