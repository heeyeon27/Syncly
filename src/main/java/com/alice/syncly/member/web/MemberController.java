package com.alice.syncly.member.web;

import com.alice.syncly.auth.web.dto.MemberCreateRequest;
import com.alice.syncly.member.domain.Member;
import com.alice.syncly.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<Member> create(@RequestBody MemberCreateRequest request) {
        Member member = memberService.createMember(
                request.getEmail(),
                request.getPassword(),
                request.getName(),
                request.getSlackUserId()
        );
        return ResponseEntity
                .created(URI.create("/api/members/" + member.getId()))
                .body(member);
    }

    @GetMapping
    public List<Member> findAll() {
        return memberService.findAll();
    }
}
