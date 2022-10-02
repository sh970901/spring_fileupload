package com.ll.exam.profileapp.app.article.repository;

import com.ll.exam.profileapp.app.article.entity.Article;

import java.util.List;

public interface ArticleRepositoryCustom {
    List<Article> getQslArticlesOrderByIdDesc();
}
