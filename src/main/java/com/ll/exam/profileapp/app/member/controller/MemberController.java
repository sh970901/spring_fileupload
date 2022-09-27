package com.ll.exam.profileapp.app.member.controller;

import com.ll.exam.profileapp.app.member.entity.Member;
import com.ll.exam.profileapp.app.member.service.MemberService;
import com.ll.exam.profileapp.app.security.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.concurrent.TimeUnit;

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

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify")
    public String showModify() {
        return "member/modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify")
    public String modify(@AuthenticationPrincipal MemberContext context, String email, MultipartFile profileImg, String profileImg__delete) {
        Member member = memberService.getMemberById(context.getId());
        if ( profileImg__delete != null && profileImg__delete.equals("Y") ) {
            memberService.removeProfileImg(member);
        }

        memberService.modify(member, email, profileImg);

        return "redirect:/member/profile";
    }

    //로그인한 회원만
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public String showProfile(){
        return "member/profile";
    }

//    @GetMapping("/profile/img/{id}")
//    public String showProfileImg(@PathVariable Long id) {
//        return "redirect:" + memberService.getMemberById(id).getProfileImgUrl();
//    } 밑에랑 거의 똑같음  httpHeaders.setCacheControl(CacheControl.maxAge(60 * 60 * 1, TimeUnit.SECONDS)); 이거 빼면 

    @GetMapping("/profile/img/{id}")
    public ResponseEntity<Object> showProfileImg(@PathVariable Long id) throws URISyntaxException {
        URI redirectUri = new URI(memberService.getMemberById(id).getProfileImgUrl());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(redirectUri);
        httpHeaders.setCacheControl(CacheControl.maxAge(60 * 60 * 1, TimeUnit.SECONDS));
        //디스크 캐시를 60초로 사용
        return new ResponseEntity<>(httpHeaders, HttpStatus.FOUND);
    }
}
