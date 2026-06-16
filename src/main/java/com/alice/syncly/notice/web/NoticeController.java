package com.alice.syncly.notice.web;

import com.alice.syncly.auth.service.MemberUserDetails;
import com.alice.syncly.notice.domain.Notice;
import com.alice.syncly.notice.service.NoticeService;
import com.alice.syncly.notice.web.dto.NoticeItemDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/notices")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @PostMapping
    public ResponseEntity<NoticeItemDto> create(
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal MemberUserDetails userDetails) {

        Long projectId = Long.valueOf(body.get("projectId").toString());
        String title   = body.get("title").toString().trim();
        String content = body.get("content").toString().trim();

        Notice saved = noticeService.create(projectId, userDetails.getMember().getId(), title, content);
        return ResponseEntity
                .created(URI.create("/api/notices/" + saved.getId()))
                .body(new NoticeItemDto(saved));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id,
                                           @AuthenticationPrincipal MemberUserDetails userDetails) {
        noticeService.markAsRead(id, userDetails.getMember().getId());
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, String>> handleBadRequest(RuntimeException e) {
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }
}
