package com.alice.syncly.member.service;

import com.alice.syncly.member.domain.ApprovalStatus;
import com.alice.syncly.member.domain.Member;
import com.alice.syncly.member.domain.Role;
import com.alice.syncly.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private static final java.util.regex.Pattern KOREAN = java.util.regex.Pattern.compile("[가-힣ㄱ-ㅎㅏ-ㅣ]");

    @Transactional
    public Member createMember(String email, String rawPassword, String name, String slackUserId) {
        if (memberRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
        if (KOREAN.matcher(rawPassword).find()) {
            throw new IllegalArgumentException("비밀번호에는 한글을 입력할 수 없습니다.");
        }
        String encoded = passwordEncoder.encode(rawPassword);
        Member member = new Member(email, encoded, name, Role.USER, slackUserId);
        return memberRepository.save(member);
    }

    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    public List<Member> searchApproved(String q, Long excludeId) {
        return memberRepository.searchByNameOrEmailAndStatus(q, ApprovalStatus.APPROVED, excludeId);
    }
}
