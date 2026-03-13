package com.alice.syncly.auth.service;

import com.alice.syncly.member.domain.ApprovalStatus;
import com.alice.syncly.member.domain.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class MemberUserDetails implements UserDetails {

    private final Member member;

    public MemberUserDetails(Member member) {
        this.member = member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleStr = member.getSystemRole() != null ? "ROLE_" + member.getSystemRole().name() : "ROLE_USER";
        return List.of(new SimpleGrantedAuthority(roleStr));
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return member.getApprovalStatus() != ApprovalStatus.REJECTED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return member.getApprovalStatus() == ApprovalStatus.APPROVED;
    }

    public Member getMember() {
        return member;
    }
}
