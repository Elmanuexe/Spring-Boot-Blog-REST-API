package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.model.Category;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.Tag;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.payload.PostRequest;
import com.sopromadze.blogapi.payload.PostResponse;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SpringSecurityTestWebConfig.class})
@AutoConfigureMockMvc
class PostControllerTest {

    @MockBean
    private PostService postService;

    @InjectMocks
    private PostController postController;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static Post post;
    private static Category category;
    private static Tag tag;
    private static PagedResponse<Post> pagedResponse;
    private static UserPrincipal userPrincipal;
    private static PostRequest postRequest;
    private static PostResponse postResponse;
    private static ApiResponse apiResponse;

    @BeforeEach
    void setUp() {

        apiResponse=new ApiResponse(true,"Se ha borrado el post");

        userPrincipal = new UserPrincipal(4L, "Pepe", "Palomo",
                "pepepalomo", "pepepalomo@gmail.com", "1234",
                Collections.singleton(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString())));

        category = new Category();
        category.setId(2L);
        category.setCreatedBy(4L);
        category.setName("Categoria1");

        postRequest = new PostRequest();
        postRequest.setCategoryId(2L);
        postRequest.setBody("BodyBodyBodyBodyBodyBodyBodyBodyBodyBodyBodyBodyBodyBodyBodyBodyBodyBodyBodyBody");
        postRequest.setTitle("PostPostPostPost");

        postResponse = new PostResponse();
        postResponse.setCategory("Categoria1");
        postResponse.setBody("BodyBodyBodyBodyBodyBodyBodyBodyBodyBodyBodyBodyBodyBodyBodyBodyBodyBodyBodyBody");
        postResponse.setTitle("PostPostPostPost");

        tag = new Tag();
        tag.setId(3L);
        tag.setName("Tag1");

        post = new Post();
        post.setId(1L);
        post.setCategory(category);
        post.setTags(List.of(tag));
        post.setCreatedAt(Instant.now());
        post.setUpdatedAt(Instant.now());

        pagedResponse = new PagedResponse(List.of(post),1,1,1,1,true);
    }

    @Test
    void getAllPosts_Success() throws Exception{

        when(postService.getAllPosts(1,1)).thenReturn(pagedResponse);

        mockMvc.perform(
                get("/api/posts")
                        .param("page","1")
                        .param("size","1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    void getPostsByCategory_Success() throws Exception{
        when(postService.getPostsByCategory(2L,1,1)).thenReturn(pagedResponse);

        mockMvc.perform(
                get("/api/posts/category/{id}", 2L)
                        .param("page","1")
                        .param("size","1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    void getPostsByTag_Success() throws Exception{
        when(postService.getPostsByTag(3L,1,1)).thenReturn(pagedResponse);

        mockMvc.perform(
                get("/api/posts/tag/{id}", 3L)
                        .param("page","1")
                        .param("size","1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    @WithUserDetails("user")
    void addPost_Success() throws Exception{
        when(postService.addPost(postRequest,userPrincipal)).thenReturn(postResponse);

        mockMvc.perform(
                post("/api/posts")
                        .contentType("application/json")
                        //.content(objectMapper.writeValueAsString(postResponse)))
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void addPost_Unauthorized() throws Exception{
        mockMvc.perform(
                post("/api/posts")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(postResponse)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getPost_Success() throws Exception {
        when(postService.getPost(1L)).thenReturn(post);

        mockMvc.perform(
                get("/api/posts/{id}",1L)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(post)))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void updatePost_Success() throws Exception {
        when(postService.updatePost(1L,postRequest,userPrincipal)).thenReturn(post);

        mockMvc.perform(
                put("/api/albums/{id}",1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(post)))
                .andExpect(status().isCreated());
    }

    @Test
    void updatePost_Unauthorized() throws Exception {
        mockMvc.perform(
                        put("/api/albums/{id}",1L)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(post)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails("admin")
    void deletePost_Success() throws Exception {
        when(postService.deletePost(1L, userPrincipal)).thenReturn(apiResponse);

        mockMvc.perform(delete("/api/albums/{id}",1L)                       )
                .andExpect(status().isOk());
    }

    @Test
    void deletePost_Unauthorized() throws Exception {
        mockMvc.perform(delete("/api/albums/{id}",1L)                       )
                .andExpect(status().isUnauthorized());
    }

}