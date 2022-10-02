package com.ll.exam.profileapp.app.article.repository;

import com.ll.exam.profileapp.app.article.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long>, ArticleRepositoryCustom{
    List<Article> findAllByOrderByIdDesc();

}
