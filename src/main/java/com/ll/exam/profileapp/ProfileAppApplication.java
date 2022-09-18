package com.ll.exam.profileapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing //BaseEntity에서 시간 자동 설정을 위해 필요함
public class ProfileAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProfileAppApplication.class, args);
    }

}
