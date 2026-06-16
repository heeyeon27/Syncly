package com.alice.syncly.member.web.dto;

import com.alice.syncly.member.domain.Member;

public class MemberSearchResponse {

    private Long id;
    private String name;
    private String email;

    public MemberSearchResponse(Member member) {
        this.id = member.getId();
        this.name = member.getName();
        this.email = member.getEmail();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}
