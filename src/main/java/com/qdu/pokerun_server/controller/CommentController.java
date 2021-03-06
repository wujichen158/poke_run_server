package com.qdu.pokerun_server.controller;

import com.qdu.pokerun_server.api.annotation.*;
import com.qdu.pokerun_server.api.ownerChecks.CommentCheck;
import com.qdu.pokerun_server.entity.Comment;
import com.qdu.pokerun_server.entity.Post;
import com.qdu.pokerun_server.entity.Player;
import com.qdu.pokerun_server.lib.LibMisc;
import com.qdu.pokerun_server.repository.CommentRepository;
import com.qdu.pokerun_server.repository.PostRepository;
import com.qdu.pokerun_server.utils.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.qdu.pokerun_server.utils.SpringUtil.genPageRequest;

@RestController
public class CommentController {
    private static final Logger log = LoggerFactory.getLogger(CommentController.class);

    @Autowired // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    static class NewCommentParams {
        public Integer postid;
        @ParamLenAnnotation.ParamLen(min = 1, max = 10240)
        public String content;
    }

    @PostMapping(path = "/api/comment", produces = LibMisc.PRODUCE_TYPE)
    @Transactional
    @Permission(Least = 1)
    public Comment newComment(@RequestBody Map<String, Object> params, HttpSession session) throws Exception {
        CheckListAnnotation.Check(new Class[]{NewCommentParams.class}, params, CheckListAnnotation.FilterPoilcy.FILTE_NOT_EXIST_NULL);
        Player user = Optional.ofNullable((Player) session.getAttribute("user")).orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
        Comment comment = new Comment();
        comment.setCreateTime(new Date());
        comment.setCreaterUid(user.getUuid());
        comment.setContent((String) params.get("content"));
        Post post = postRepository.findById(((Integer) params.get("postid")).longValue()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        comment.setPostid(post.getPostid());
        post.setComments(post.getComments() + 1);
        return commentRepository.save(comment);
    }

    @GetMapping(path = "/api/comment", produces = LibMisc.PRODUCE_TYPE)
    @Transactional
    public List<Comment> getComment(
            @RequestParam @Contains({LibMisc.FILTER_UID, LibMisc.FILTER_POSTID}) String filter,
            @RequestParam long filterId,
            @RequestParam long pageIndex,
            @RequestParam @Contains({"30", "50", "100"}) String pageSize,
            @RequestParam @Contains({"asc", "desc"}) String order
    ) {
        var pageRequest = genPageRequest(pageIndex, pageSize, order);
        if (LibMisc.FILTER_POSTID.equals(filter)) {
            Post post = postRepository.findById(filterId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
            post.setVisit(post.getVisit() + 1);
            return commentRepository.findAllByPostid(filterId, pageRequest).toList();
        } else if (LibMisc.FILTER_UID.equals(filter)) {
            return commentRepository.findAllByCreaterUid(filterId, pageRequest).toList();
        } else {
            throw new RuntimeException("Invalid filter type");
        }
    }


    static class AlterCommentConstraint {
        @ParamOptional
        @ParamLenAnnotation.ParamLen(min = 1, max = 10240)
        public String content;
    }

    @PatchMapping(path = "/api/comment/{cid}", produces = LibMisc.PRODUCE_TYPE)
    @Permission(Least = 254, Auth = CommentCheck.class)
    @Transactional
    public void alterComment(
            @RequestBody Map<String, Object> properties,
            @PathVariable @ParamOCheck long cid,
            HttpServletResponse response
    ) throws Exception {

        CheckListAnnotation.Check(new Class[]{AlterCommentConstraint.class}, properties, CheckListAnnotation.FilterPoilcy.FILTE_NOT_EXIST);
        Comment newComment = commentRepository.findById(cid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));
        SpringUtil.CopyProperties(properties, newComment);

        try {
            commentRepository.save(newComment);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping(path = "/api/comment/{cid}/like", produces = LibMisc.PRODUCE_TYPE)
    @Permission(Least = 1)
    @Transactional
    public void addLikeComment(@PathVariable long cid, HttpServletResponse response) {
        Comment comment = commentRepository.findById(cid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));
        comment.setLike(comment.getLike() + 1);
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

    @DeleteMapping(path = "/api/comment/{cid}", produces = LibMisc.PRODUCE_TYPE)
    @Permission(Least = 254, Auth = CommentCheck.class)
    @Transactional
    public void deleteComment(@PathVariable @ParamOCheck long cid, HttpServletResponse response) {
        Comment comment = commentRepository.findById(cid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));
        try {
            Post post = postRepository.findById(comment.getPostid()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
            post.setComments(post.getComments() - 1);
            commentRepository.deleteById(cid);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

}
