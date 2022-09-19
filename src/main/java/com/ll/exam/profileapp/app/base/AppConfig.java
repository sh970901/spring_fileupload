package com.ll.exam.profileapp.app.base;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    public static String GET_FILE_DIR_PATH;

    @Value("c:/temp/app10")
    public void setFileDirPath(String genFileDirPath) {
        GET_FILE_DIR_PATH = genFileDirPath;
    };
}
