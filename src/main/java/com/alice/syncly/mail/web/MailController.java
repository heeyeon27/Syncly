package com.alice.syncly.mail.web;

import com.alice.syncly.mail.service.MailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/mail")
public class MailController {

    private final MailService mailService;

    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping("/slack-invite")
    public ResponseEntity<?> sendSlackInvite(@RequestParam String email) {
        if (email == null || !email.toLowerCase().endsWith("@gmail.com")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Gmail(@gmail.com) 계정만 사용 가능합니다."));
        }
        try {
            mailService.sendSlackInvite(email);
            return ResponseEntity.ok(Map.of("message", "초대 메일을 발송했습니다."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "메일 발송에 실패했습니다. 잠시 후 다시 시도해주세요."));
        }
    }
}
