package com.ll.exam.profileapp.app.hashTag.service;

import com.ll.exam.profileapp.app.article.entity.Article;
import com.ll.exam.profileapp.app.hashTag.entity.HashTag;
import com.ll.exam.profileapp.app.hashTag.repository.HashTagRepository;
import com.ll.exam.profileapp.app.keyword.entity.Keyword;
import com.ll.exam.profileapp.app.keyword.service.KeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HashTagService {
    private final KeywordService keywordService;
    private final HashTagRepository hashTagRepository;
    public void applyHashTags(Article article, String keywordContentsStr) {
        List<String> keywordContents = Arrays.stream(keywordContentsStr.split("#"))
                .map(String :: trim)
                .filter(s -> s.length() >0)
                .collect(Collectors.toList());

        keywordContents.forEach(keywordContent -> {
            saveHashTag(article, keywordContent);
        });

    }

    private HashTag saveHashTag(Article article, String keywordContent) {
        Keyword keyword = keywordService.save(keywordContent);
        Optional<HashTag> opHashTag = hashTagRepository.findByArticleIdAndKeywordId(article.getId(), keyword.getId());
        if(opHashTag.isPresent()){
            return opHashTag.get();
        }
        HashTag hashTag = HashTag.builder()
                .article(article)
                .keyword(keyword)
                .build();

        hashTagRepository.save(hashTag);

        return hashTag;
    }
}
