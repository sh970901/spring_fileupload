package com.ll.exam.profileapp.app.base;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Value("${custom.genFileDirPath}")
    private String genFileDirPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //주소가 1로 왔을때 2에서 찾는다
        //http://localhost:8010/gen-file/**로 들어오면 c:/temp/app10/** 여기서 찾는다.
        registry.addResourceHandler("/gen/**")
                .addResourceLocations("file:///" + genFileDirPath + "/");
    }
}
