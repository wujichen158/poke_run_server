package com.qdu.pokerun_server.entity;

import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "comment")
@DynamicInsert
public class Comment {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long cid;
    @Column(nullable = false)
    private String content;
    @Column(name = "create_time")
    private Date createTime;
    private Long postid;
    @Column(name = "creater_uid", nullable = false)
    private Long createrUid;
    @Column(name = "`like`")
    private Long like = Long.valueOf(0);

    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getPostid() {
        return postid;
    }

    public void setPostid(Long postid) {
        this.postid = postid;
    }

    public Long getCreaterUid() {
        return createrUid;
    }

    public void setCreaterUid(Long createrUid) {
        this.createrUid = createrUid;
    }

    public Long getLike() {
        return like;
    }

    public void setLike(Long like) {
        this.like = like;
    }
}
