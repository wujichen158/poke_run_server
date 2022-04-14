package com.qdu.pokerun_server.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.qdu.pokerun_server.api.annotation.*;
import com.qdu.pokerun_server.entity.*;
import com.qdu.pokerun_server.lib.LibMisc;
import com.qdu.pokerun_server.repository.CommentRepository;
import com.qdu.pokerun_server.repository.PostRepository;
import com.qdu.pokerun_server.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

import org.springframework.web.server.ResponseStatusException;

@RestController
public class PostController {

    static class NewPostParams {
        @ParamLenAnnotation.ParamLen(min = 1, max = 100)
        public String title;
        @ParamLenAnnotation.ParamLen(min = 1, max = 10240)
        public String content;
        @ParamOptional
        public List<Object> tags;
    }

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TagRepository tagRepository;

    @Permission(Least = 1)
    @PostMapping(path = "/api/post", produces = LibMisc.PRODUCE_TYPE)
    @JsonView(Post.WithoutVisible.class)
    @Transactional
    public Post newPost(@RequestBody Map<String, Object> params, HttpSession session) throws Exception {
        CheckListAnnotation.Check(new Class[]{NewPostParams.class}, params, CheckListAnnotation.FilterPoilcy.FILTE_NOT_EXIST_NULL);
        System.out.println(params);
        Post post = new Post();
        var user = Optional.ofNullable((Player) session.getAttribute("user"));
        var uid = user.orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR)).getUuid();
        post.setCreaterUid(uid);
        post.setCreateTime(new Date());
        post.setTitle((String) params.get("title"));
        post.setComments(Long.valueOf(1));

        //获取并插入帖子对应tag的业务逻辑。已在Post实体内映射关系
        List<Object> tagidList = Optional.ofNullable((List<Object>) params.get("tags")).orElse(new ArrayList<>());
        List<Tag> tagList = new ArrayList<>();
        for (Object o : tagidList) {
//            System.out.println("好耶！！！！！！！！！！！！！！！！！！！");
//            tagRepository.findAll().forEach(i->System.out.println(i.getTagid()));
//            System.out.println("好耶！！！！！！！！！！！！！！！！！！！");
            Tag tag = tagRepository.findById(Long.valueOf(
                    (o instanceof Long) ? (Long) o : (Integer) o
            )).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found"));
            tag.setPosts(tag.getPosts() + 1);
            tagList.add(tag);
        }

        post.setTagList(tagList);
        try {
            post = postRepository.save(post);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Comment comment = new Comment();
        comment.setContent((String) params.get("content"));
        comment.setCreaterUid(uid);
        comment.setCreateTime(new Date());
        comment.setPostid(post.getPostid());

        try {
            commentRepository.save(comment);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return post;
    }

    private List<Post> setPostsTaglist(List<Post> postList) {
        postList.forEach(post -> post.setTagList(tagRepository.findTagByPostid(post.getPostid())));
        return postList;
    }

    @GetMapping(path = "/api/post/{postid}", produces = LibMisc.PRODUCE_TYPE)
    @JsonView(Post.WithoutVisible.class)
    public Post getPost(@PathVariable long postid) {
        Post post = postRepository.findById(postid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        post.setVisit(post.getVisit() + 1);
        return post;
    }

    @GetMapping(path = "/api/post", produces = LibMisc.PRODUCE_TYPE)
    @JsonView(Post.WithoutVisible.class)
    public List<Post> getPostByFilter(
            @RequestParam @Contains({"uid", "tagid", "null"}) String filter,
            @RequestParam long filterId,
            @RequestParam long pageIndex,
            @RequestParam @Contains({"30", "50", "100"}) String pageSize,
            @RequestParam @Contains({"asc", "desc"}) String order
    ) throws Exception {
        var direct = "asc".equals(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        PageRequest pageRequest;
        List<Post> postList;
        if (LibMisc.FILTER_UID.equals(filter)) {
            // default uid
            pageRequest = PageRequest.of((int) pageIndex, Integer.parseInt(pageSize), direct, "createTime");
            postList = postRepository.findAllByCreaterUidAndVisible(filterId, true, pageRequest).toList();
        } else if (LibMisc.FILTER_TAGID.equals(filter)) {
            pageRequest = PageRequest.of((int) pageIndex, Integer.parseInt(pageSize), direct, "create_time");
            postList = postRepository.findAllByTagidAndVisible(filterId, true, pageRequest).toList();
        } else if (LibMisc.FILTER_NULL.equals(filter)) {
            pageRequest = PageRequest.of((int) pageIndex, Integer.parseInt(pageSize), direct, "createTime");
            postList = postRepository.findAll(pageRequest).toList();
        } else {
            throw new Exception("Invalid filter type");
        }
        return setPostsTaglist(postList);
    }

    @DeleteMapping(path = "/api/post/{postid}", produces = LibMisc.PRODUCE_TYPE)
    @Permission(Least = 254)
    @Transactional
    public void deletePost(@PathVariable long postid, HttpServletResponse response) {
        List<Tag> tagList = tagRepository.findTagByPostid(postid);
        tagList.forEach(tag -> tag.setPosts(tag.getPosts() - 1));
        postRepository.findById(postid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)).setVisible(false);
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

}
