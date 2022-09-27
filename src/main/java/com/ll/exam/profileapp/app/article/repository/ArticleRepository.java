package com.ll.exam.profileapp.app.article.repository;

import com.ll.exam.profileapp.app.article.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}
