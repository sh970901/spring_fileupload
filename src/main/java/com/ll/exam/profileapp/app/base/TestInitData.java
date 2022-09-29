package com.ll.exam.profileapp.app.base;

import com.ll.exam.profileapp.app.article.entity.Article;
import com.ll.exam.profileapp.app.article.service.ArticleService;
import com.ll.exam.profileapp.app.member.entity.Member;
import com.ll.exam.profileapp.app.member.service.MemberService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("test") // 이 클래스 정의된 Bean 들은 test 모드에서만 활성화 된다.
public class TestInitData {
    // CommandLineRunner : 주로 앱 실행 직후 초기데이터 세팅 및 초기화에 사용
    @Bean
    CommandLineRunner init(MemberService memberService, ArticleService articleService, PasswordEncoder passwordEncoder) {
        return args -> {
            String password = passwordEncoder.encode("1234");
            Member member1 = memberService.join("user1", password, "user1@test.com");
            Member member2 = memberService.join("user2", password, "user2@test.com");
            Member member3 = memberService.join("user3", password, "user3@test.com");
            Member member4 = memberService.join("user4", password, "user4@test.com");

            Article article1 = articleService.write(member1, "제목1", "내용1", "#자바 #프로그래밍");
            Article article2 = articleService.write(member1, "제목2", "내용2", "#HTML #프로그래밍");
        };
    }
}
