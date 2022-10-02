package com.ll.exam.profileapp.app.article.input;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Setter
@Getter
public class ArticleForm {
    @NotEmpty
    private String subject;
    @NotEmpty
    private String content;
    private String hashTagContents;
}
