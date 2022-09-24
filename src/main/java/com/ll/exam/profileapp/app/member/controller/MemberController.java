package com.ll.exam.profileapp.app.member.controller;

import com.ll.exam.profileapp.app.member.entity.Member;
import com.ll.exam.profileapp.app.member.service.MemberService;
import com.ll.exam.profileapp.app.security.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

    private final PasswordEncoder passwordEncoder;

//    로그인 안된 사람만
    @PreAuthorize("isAnonymous()")
    @GetMapping("/join")
    public String showJoin(){
        return "member/join";
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/login")
    public String showLogin(){
        return "member/login";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/join")
    public String join(HttpServletRequest req, String username, String password, String email, MultipartFile profileImg){
        Member oldMember = memberService.getMemberByUsername(username);
        String passwordClearText=password;
        password = passwordEncoder.encode(password);

        if (oldMember != null) {
            return "redirect:/?errorMsg=Already done.";
        }

        Member member = memberService.join(username, password, email, profileImg);
        try {
            req.login(username, passwordClearText);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

        return "redirect:/member/profile";
    }

    //로그인한 회원만
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public String showProfile(){
        return "member/profile";
    }

    @GetMapping("/profile/img/{id}")
    public String showProfileImg(@PathVariable Long id) {
        return "redirect:" + memberService.getMemberById(id).getProfileImgUrl();
    }
}
