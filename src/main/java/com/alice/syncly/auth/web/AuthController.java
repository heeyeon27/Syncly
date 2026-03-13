package com.alice.syncly.auth.web;

import com.alice.syncly.auth.web.dto.MemberCreateRequest;
import com.alice.syncly.member.domain.Member;
import com.alice.syncly.member.service.MemberService;
import com.alice.syncly.slack.service.SlackNotifierService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final MemberService memberService;
    private final SlackNotifierService slackNotifierService;

    public AuthController(MemberService memberService, SlackNotifierService slackNotifierService) {
        this.memberService = memberService;
        this.slackNotifierService = slackNotifierService;
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("member", new MemberCreateRequest());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute MemberCreateRequest request,
                         RedirectAttributes redirectAttributes) {
        try {
            Member saved = memberService.createMember(
                    request.getEmail(),
                    request.getPassword(),
                    request.getName(),
                    request.getSlackUserId()
            );
            slackNotifierService.sendSignupNotification(saved.getId(), saved.getName(), saved.getEmail());
            return "redirect:/login?signupSuccess";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/signup";
        }
    }
}
