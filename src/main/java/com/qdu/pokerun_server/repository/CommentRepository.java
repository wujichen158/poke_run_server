package com.qdu.pokerun_server.repository;

import com.qdu.pokerun_server.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface CommentRepository extends CrudRepository<Comment, Long> {
    Page<Comment> findByPostid(Long postid, Pageable pageable);
    Page<Comment> findAllByPostid(Long postid, Pageable pageable);
    Page<Comment> findAllByCreaterUid(Long createrUid, Pageable pageable);
//    Page<Comment> findAllByTagid();
}
