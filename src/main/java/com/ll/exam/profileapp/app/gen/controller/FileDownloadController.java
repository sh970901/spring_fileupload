package com.ll.exam.profileapp.app.gen.controller;

import com.ll.exam.profileapp.app.gen.entity.GenFile;
import com.ll.exam.profileapp.app.gen.service.GenFileService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

@Controller
@RequestMapping("/download")
@RequiredArgsConstructor
public class FileDownloadController {
    private final GenFileService genFileService;

    @SneakyThrows
    @GetMapping("/gen/{id}")
    public void download(HttpServletResponse response, @PathVariable Long id) {
        GenFile genFile = genFileService.getById(id).get();

        String path = genFile.getFilePath();

        File file = new File(path);
        response.setHeader("Content-Disposition", "attachment;filename=" + genFile.getOriginFileName());

        FileInputStream fileInputStream = new FileInputStream(path); // 파일 읽어오기
        OutputStream out = response.getOutputStream();

        int read = 0;
        byte[] buffer = new byte[1024];
        while ((read = fileInputStream.read(buffer)) != -1) { // 1024바이트씩 계속 읽으면서 outputStream에 저장, -1이 나오면 더이상 읽을 파일이 없음
            out.write(buffer, 0, read);
        }
    }
}