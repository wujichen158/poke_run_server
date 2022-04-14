package com.qdu.pokerun_server.repository;

import com.qdu.pokerun_server.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TagRepository extends CrudRepository<Tag, Long> {

    @Deprecated
    @Modifying
    @Query(value = "insert into post_tag(tagid, postid) values(?1, ?2)", nativeQuery = true)
    public void updatePostTag(Long tagid, Long postid);

    @Query(value = "select * from tag where tagid in(select tagid from post_tag where postid = ?1)", nativeQuery = true)
    public List<Tag> findTagByPostid(Long postid);

    Page<Tag> findAll(Pageable pageable);

//    public List<Tag> findByPostid(Long postid);
}
