package org.namph.blog.entity;

import javax.persistence.*;

@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "metaTitle")
    private String metaTitle;

    @Column(name = "slug")
    private String slug;

    @Column(name = "content")
    private String content;
}
