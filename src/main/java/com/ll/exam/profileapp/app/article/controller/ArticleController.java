package com.ll.exam.profileapp.app.article.controller;

import com.ll.exam.profileapp.app.Util;
import com.ll.exam.profileapp.app.article.entity.Article;
import com.ll.exam.profileapp.app.article.input.ArticleForm;
import com.ll.exam.profileapp.app.article.service.ArticleService;
import com.ll.exam.profileapp.app.base.dto.RsData;
import com.ll.exam.profileapp.app.fileUpload.entity.GenFile;
import com.ll.exam.profileapp.app.fileUpload.service.GenFileService;
import com.ll.exam.profileapp.app.security.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import javax.validation.Valid;
import java.util.Map;

@Controller
@RequestMapping("/article")
@Slf4j
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;
    private final GenFileService genFileService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/write")
    public String showWrite() {
        return "article/write";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/write")
    public String write(@AuthenticationPrincipal MemberContext memberContext, @Valid ArticleForm articleForm, MultipartRequest multipartRequest, BindingResult bindingResult) {
        //여러 사진 받을 수 있음
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();

        log.debug("fileMap : " + fileMap);

        Article article = articleService.write(memberContext.getId(), articleForm.getSubject(), articleForm.getContent());

        RsData<Map<String, GenFile>> saveFilesRsData = genFileService.saveFiles(article, fileMap);

        log.debug("saveFilesRsData : " + saveFilesRsData);

        String msg = "%d번 게시물이 작성되었습니다.".formatted(article.getId());
        msg = Util.url.encode(msg);
        return "redirect:/article/%d?msg=%s".formatted(article.getId(), msg);
    }
    @GetMapping("/{id}")
    public String showDetail(Model model, @PathVariable Long id) {
        Article article = articleService.getArticleById(id);
        model.addAttribute("article", article);

        return "article/detail";
    }

}
