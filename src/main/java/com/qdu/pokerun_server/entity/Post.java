package com.qdu.pokerun_server.entity;

import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity // This tells Hibernate to make a table out of this class
@DynamicUpdate
@Table(name = "post")
public class Post {

    public interface WithoutVisible {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postid;
    @Column(nullable = false)
    private String title;
    @Column(name = "creater_uid")
    private Long createrUid;

    @Column(name = "create_time")
    private Date createTime;
    private Long visit = Long.valueOf(0);
    @Column(nullable = false)
    private boolean visible = true;
    private Long comments = Long.valueOf(0);

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "post_tag",
            joinColumns = @JoinColumn(name = "postid"),
            inverseJoinColumns = @JoinColumn(name = "tagid"),
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private List<Tag> tagList;

    @JsonView(WithoutVisible.class)
    public List<Tag> getTagList() {
        return this.tagList;
    }

    public void setTagList(List<Tag> tagList) {
        this.tagList = tagList;
    }

    @JsonView(WithoutVisible.class)
    public Long getPostid() {
        return postid;
    }

    public void setPostid(Long postid) {
        this.postid = postid;
    }

    @JsonView(WithoutVisible.class)
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @JsonView(WithoutVisible.class)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonView(WithoutVisible.class)
    public Long getCreaterUid() {
        return createrUid;
    }

    public void setCreaterUid(Long createrUid) {
        this.createrUid = createrUid;
    }

    @JsonView(WithoutVisible.class)
    public Long getVisit() {
        return visit;
    }

    public void setVisit(Long visit) {
        this.visit = visit;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @JsonView(WithoutVisible.class)
    public Long getComments() {
        return comments;
    }

    public void setComments(Long comments) {
        this.comments = comments;
    }
}
