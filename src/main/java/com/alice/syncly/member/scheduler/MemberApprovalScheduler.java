package com.alice.syncly.member.scheduler;

import com.alice.syncly.member.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class MemberApprovalScheduler {

    private static final Logger log = LoggerFactory.getLogger(MemberApprovalScheduler.class);

    private final MemberRepository memberRepository;

    public MemberApprovalScheduler(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /** 1분마다 실행 - 가입 후 5분이 지난 PENDING 회원을 APPROVED로 자동 전환 */
    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void autoApprovePendingMembers() {
        LocalDateTime now    = LocalDateTime.now();
        LocalDateTime cutoff = now.minusMinutes(5);

        int count = memberRepository.approveExpiredPendingMembers(cutoff, now);

        if (count > 0) {
            log.info("[MemberApprovalScheduler] PENDING → APPROVED 자동 전환 완료 - {}명", count);
        } else {
            log.debug("[MemberApprovalScheduler] 전환 대상 없음 (기준시각={})", cutoff);
        }
    }
}
