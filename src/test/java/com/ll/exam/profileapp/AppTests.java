package com.ll.exam.profileapp;

import com.ll.exam.profileapp.app.home.controller.HomeController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
class AppTests {
    @Autowired
    private MockMvc mvc;

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
                .andExpect(handler().methodName("main"))            //메서드명 지정
                .andExpect(content().string(containsString("안녕")));
    }

}
