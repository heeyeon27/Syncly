package com.alice.syncly.notice.service;

import com.alice.syncly.member.domain.Member;
import com.alice.syncly.member.repository.MemberRepository;
import com.alice.syncly.notice.domain.Notice;
import com.alice.syncly.notice.domain.NoticeRead;
import com.alice.syncly.notice.domain.NoticeReadId;
import com.alice.syncly.notice.repository.NoticeReadRepository;
import com.alice.syncly.notice.repository.NoticeRepository;
import com.alice.syncly.notice.web.dto.NoticeItemDto;
import com.alice.syncly.project.domain.Project;
import com.alice.syncly.project.domain.ProjectMember;
import com.alice.syncly.project.repository.ProjectMemberRepository;
import com.alice.syncly.project.repository.ProjectRepository;
import com.alice.syncly.slack.service.SlackNotifierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class NoticeService {

    private static final Logger log = LoggerFactory.getLogger(NoticeService.class);

    private final NoticeRepository noticeRepository;
    private final NoticeReadRepository noticeReadRepository;
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final SlackNotifierService slackNotifierService;

    public NoticeService(NoticeRepository noticeRepository,
                         NoticeReadRepository noticeReadRepository,
                         ProjectRepository projectRepository,
                         MemberRepository memberRepository,
                         ProjectMemberRepository projectMemberRepository,
                         SlackNotifierService slackNotifierService) {
        this.noticeRepository        = noticeRepository;
        this.noticeReadRepository    = noticeReadRepository;
        this.projectRepository       = projectRepository;
        this.memberRepository        = memberRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.slackNotifierService    = slackNotifierService;
    }

    @Transactional
    public Notice create(Long projectId, Long authorId, String title, String content) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
        Member author = memberRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found: " + authorId));

        Notice notice = new Notice(title, content, project, author);
        Notice saved = noticeRepository.save(notice);

        List<ProjectMember> members = projectMemberRepository.findByProjectWithMember(project);
        log.info("[Notice] 공지 저장 완료 - 프로젝트: {}, 알림 대상: {}명", project.getName(), members.size());

        slackNotifierService.sendNoticeCreated(project.getName(), title, author.getName(), members);

        return saved;
    }

    public List<NoticeItemDto> findByProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
        return noticeRepository.findByProjectWithAuthorOrderByCreatedAtDesc(project)
                .stream()
                .map(NoticeItemDto::new)
                .collect(Collectors.toList());
    }

    public List<NoticeItemDto> findUnreadByMember(Long memberId) {
        return noticeRepository.findUnreadByMember(memberId)
                .stream()
                .map(NoticeItemDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(Long noticeId, Long memberId) {
        NoticeReadId readId = new NoticeReadId(noticeId, memberId);
        if (!noticeReadRepository.existsById(readId)) {
            noticeReadRepository.save(new NoticeRead(noticeId, memberId));
            log.info("[Notice] 읽음 처리 - noticeId: {}, memberId: {}", noticeId, memberId);
        }
    }
}
