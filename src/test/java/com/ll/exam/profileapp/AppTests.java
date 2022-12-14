package com.ll.exam.profileapp;

import com.ll.exam.profileapp.app.home.controller.HomeController;
import com.ll.exam.profileapp.app.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional  //여기서 수행된건 디비반영X
@AutoConfigureMockMvc
@ActiveProfiles("test") //application.yml에 active: dev, base-addi가 열리는게 아니고 "base-addi", "test" 얘네가 열린다.
class AppTests {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("메인화면에서는 안녕이 나와야 한다.")
    void t1() throws Exception {
        //WHEN
        //GET http://localhost:8010
        ResultActions resultActions = mvc   //mvc는 일종의 브라우저
                .perform(get("/"))
                .andDo(print());
        //THEN
        //안녕
        resultActions
                .andExpect(status().is2xxSuccessful()) //200성공 300리다이렉드 400클라잘못 500서버잘못
                .andExpect(handler().handlerType(HomeController.class))     //컨트롤러 지정
                .andExpect(handler().methodName("showMain"))            //메서드명 지정
                .andExpect(content().string(containsString("안녕")));
    }
//    @Test
//    @DisplayName("회원의 수")
//    void t2() throws Exception {
//        long count = memberService.count();
//        assertThat(count).isGreaterThan(0);
//    }
//
//    @Test
//    @DisplayName("user1로 로그인 후 프로필페이지에 접속하면 user1의 이메일이 보여야 한다.")
//    @WithUserDetails("user1")
//    void t3() throws Exception {
//        //mockMvc로 로그인 처리
//        // WHEN
//        // GET /
//        ResultActions resultActions = mvc
//                .perform(
//                        get("/member/profile")
//                )
//                .andDo(print());
//
//        // THEN
//        // 안녕
//        resultActions
//                .andExpect(status().is2xxSuccessful())
//                .andExpect(handler().handlerType(MemberController.class))
//                .andExpect(handler().methodName("showProfile"))
//                .andExpect(content().string(containsString("user1@test.com")));
//
//    }
//    @Test
//    @DisplayName("user4로 로그인 후 프로필페이지에 접속하면 user4의 이메일이 보여야 한다.")
//    @WithUserDetails("user4")
//    void t4() throws Exception {
//    //mockMvc로 로그인 처리
//        // WHEN
//        // GET /
//        ResultActions resultActions = mvc
//                .perform(
//                        get("/member/profile")
//                )
//                .andDo(print());
//
//        // THEN
//        // 안녕
//        resultActions
//                .andExpect(status().is2xxSuccessful())
//                .andExpect(handler().handlerType(MemberController.class))
//                .andExpect(handler().methodName("showProfile"))
//                .andExpect(content().string(containsString("user4@test.com")));
//    }
//    @Test
//    @DisplayName("회원가입")
//    void t5() throws Exception {
//        String testUploadFileUrl = "https://picsum.photos/200/300";
//        String originalFileName = "test.png";
//
//        // wget
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<Resource> response = restTemplate.getForEntity(testUploadFileUrl, Resource.class);
//        InputStream inputStream = response.getBody().getInputStream();
//
//        MockMultipartFile profileImg = new MockMultipartFile(
//                "profileImg",
//                originalFileName,
//                "image/png",
//                inputStream
//        );
//
//        // 회원가입(MVC MOCK)
//        // when
//        ResultActions resultActions = mvc.perform(
//                        multipart("/member/join")
//                                .file(profileImg)
//                                .param("username", "user5")
//                                .param("password", "1234")
//                                .param("email", "user5@test.com")
//                                .characterEncoding("UTF-8"))
//                .andDo(print());
//
//        resultActions
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/member/profile"))
//                .andExpect(handler().handlerType(MemberController.class))
//                .andExpect(handler().methodName("join"));
//
//        Member member = memberService.getMemberById(5L);
//
//        assertThat(member).isNotNull();
//
//        memberService.removeProfileImg(member);
//    }

}
