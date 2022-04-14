package com.qdu.pokerun_server.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import com.qdu.pokerun_server.entity.*;

import org.springframework.data.domain.Pageable;

public interface PostRepository extends CrudRepository<Post, Long> {
    Page<Post> findAllByCreaterUidAndVisible(Long uid, boolean visible, Pageable pageable);

    @Query(value = "select * from post where visible = ?2 and postid in(select postid from post_tag where tagid = ?1)", nativeQuery = true)
    Page<Post> findAllByTagidAndVisible(Long tagid, boolean visible, Pageable pageable);

    Page<Post> findAll(Pageable pageable);
}
