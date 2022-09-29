package com.ll.exam.profileapp.app.article.service;

import com.ll.exam.profileapp.app.article.entity.Article;
import com.ll.exam.profileapp.app.article.repository.ArticleRepository;
import com.ll.exam.profileapp.app.fileUpload.entity.GenFile;
import com.ll.exam.profileapp.app.fileUpload.service.GenFileService;
import com.ll.exam.profileapp.app.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final GenFileService genFileService;

    public Article write(Long authorId, String subject, String content) {
        return write(new Member(authorId), subject, content);
    }
    public Article write(Member member, String subject, String content) {
        Article article = Article
                .builder()
                .author(member)
                .subject(subject)
                .content(content)
                .build();

        articleRepository.save(article);

        return article;
    }

    public Article getArticleById(Long id) {
        return articleRepository.findById(id).orElse(null);
    }


    public void addGenFileByUrl(Article article, String typeCode, String type2Code, int fileNo, String url) {
        genFileService.addGenFileByUrl("article", article.getId(), typeCode, type2Code, fileNo, url);
    }
    public Article getForPrintArticleById(Long id) {
        Article article = getArticleById(id);

        Map<String, GenFile> genFileMap = genFileService.getRelGenFileMap(article);

        article.getExtra().put("age__name__33", 22);
        article.getExtra().put("genFileMap", genFileMap);

        return article;
    }
}
