package com.alice.syncly.member.repository;

import com.alice.syncly.member.domain.ApprovalStatus;
import com.alice.syncly.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT m FROM Member m WHERE m.approvalStatus = :status " +
           "AND m.id != :excludeId " +
           "AND (LOWER(m.name) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(m.email) LIKE LOWER(CONCAT('%', :q, '%')))")
    List<Member> searchByNameOrEmailAndStatus(@Param("q") String q,
                                              @Param("status") ApprovalStatus status,
                                              @Param("excludeId") Long excludeId);
}
