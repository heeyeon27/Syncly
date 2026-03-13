package com.alice.syncly.config;

import com.alice.syncly.member.domain.ApprovalStatus;
import com.alice.syncly.member.domain.Member;
import com.alice.syncly.member.domain.Role;
import com.alice.syncly.member.repository.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initTestMember(MemberRepository memberRepository,
                                            PasswordEncoder passwordEncoder) {
        return args -> {
            if (memberRepository.findByEmail("test@test.com").isPresent()) {
                return; // 이미 존재하면 스킵
            }

            Member member = new Member(
                    "test@test.com",
                    passwordEncoder.encode("1234"),
                    "테스트유저",
                    Role.USER,
                    "test-slack-user-id"
            );
            member.setApprovalStatus(ApprovalStatus.APPROVED);
            memberRepository.save(member);

            System.out.println("[DataInitializer] 테스트 계정 생성 완료: test@test.com");
        };
    }
}
