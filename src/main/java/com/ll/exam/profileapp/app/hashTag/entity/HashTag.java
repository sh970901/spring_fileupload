package com.ll.exam.profileapp.app.hashTag.entity;

import com.ll.exam.profileapp.app.article.entity.Article;
import com.ll.exam.profileapp.app.base.entity.BaseEntity;
import com.ll.exam.profileapp.app.keyword.entity.Keyword;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class HashTag extends BaseEntity {
    @ManyToOne
    @ToString.Exclude
    private Article article;

    @ManyToOne
    @ToString.Exclude
    private Keyword keyword;
}
