package org.namph.blog.controller;

import org.namph.blog.entity.Meta;
import org.namph.blog.entity.Post;
import org.namph.blog.repository.MetaRepository;
import org.namph.blog.repository.PostRepository;
import org.namph.blog.request.PostRequest;
import org.namph.blog.service.PostService;
import org.namph.blog.util.ResponseBodyUtil;
import org.namph.blog.util.ResponseStatusUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * author: namph
 * date: 16/08/2021
 */
@RestController
@RequestMapping("post")
public class PostController {
    private static final String FIND_ALL_FLG = "-1";

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MetaRepository metaRepository;

    @GetMapping
    public ResponseEntity getPost(@RequestParam(defaultValue = FIND_ALL_FLG) int id) {
        ResponseBodyUtil responseBodyUtil = new ResponseBodyUtil();

        if(id == Integer.parseInt(FIND_ALL_FLG)) {
            List allPost = new ArrayList();
            allPost.addAll(postRepository.getAllPostIntro());
            responseBodyUtil.setData(allPost);
        } else {
            Post post = postService.getPost(id);
            responseBodyUtil.setData(post);
        }

        responseBodyUtil.setStatus(ResponseStatusUtil.SUCCESS);
        return ResponseEntity.ok().body(responseBodyUtil);
    }

    @PostMapping
    public ResponseEntity newPost(@Valid @RequestBody PostRequest postRequest) {
        Post post = new Post();

        post.setAuthorId(postRequest.getAuthorId());
        post.setContent(postRequest.getContent());
        post.setMetaTitle(postRequest.getMetaTitle());
        post.setSlug(postRequest.getSlug());
        post.setTitle(postRequest.getTitle());
        post.setSummary(postRequest.getTitle());

        LocalDateTime createAt = LocalDateTime.now();
        post.setCreateAt(createAt);
        post.setUpdateAt(LocalDateTime.now());
        post.setPublishedAt(LocalDateTime.now());
        post.setPublished(true);

        // save post
        int result = postRepository.saveNewPost(post);

        // find firs image url
        String image = postService.findImageUrl(postRequest.getContent());
        Meta meta = new Meta();
        int postId = postRepository.findIdByCreateAt(createAt);
        meta.setPostId(postId);
        meta.setKey(Meta.IMAGE_KEY);
        meta.setContent(image);

        //save meta
        metaRepository.saveMeta(meta);

        ResponseBodyUtil responseBodyUtil = new ResponseBodyUtil();
        HttpStatus httpStatus;
        if(result > 0) {
            responseBodyUtil.setStatus(ResponseStatusUtil.SUCCESS);
            httpStatus = HttpStatus.OK;
        } else {
            responseBodyUtil.setStatus(ResponseStatusUtil.FAIL);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        responseBodyUtil.setStatus(ResponseStatusUtil.SUCCESS);

        return ResponseEntity.status(httpStatus).body(responseBodyUtil);
    }
}
