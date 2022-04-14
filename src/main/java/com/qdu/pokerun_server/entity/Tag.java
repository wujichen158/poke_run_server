package com.qdu.pokerun_server.entity;

import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Entity(name = "tag")
@DynamicInsert
public class Tag {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long tagid;
//    @Column(nullable = false)
    private String name;
    private Integer posts = Integer.valueOf(0);

    @JsonView(Post.WithoutVisible.class)
    public Long getTagid() {
        return tagid;
    }

    public void setTagid(Long tagid) {
        this.tagid = tagid;
    }

    @JsonView(Post.WithoutVisible.class)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPosts() {
        return posts;
    }

    public void setPosts(Integer posts) {
        this.posts = posts;
    }
}
