package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.exception.BadRequestException;
import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.Comment;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.Tag;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.CommentRequest;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.CommentService;
import javassist.NotFoundException;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Log
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SpringSecurityTestWebConfig.class})
@AutoConfigureMockMvc
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService service;


    @Test
    void getAllComments_success() throws Exception {

        Comment c = new Comment();
        c.setName("comentario");
        c.setBody("bodybodybodybody");
        c.setEmail("email");
        c.setId(1L);

        PagedResponse<Comment> comentarios = new PagedResponse();
        comentarios.setContent(List.of(c));

        when(service.getAllComments(any(Long.class), any(Integer.class), any(Integer.class))).thenReturn(comentarios);

        MvcResult result = mockMvc.perform(get("/api/posts/{postId}/comments", 1L))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andReturn();

        log.info(result.getResponse().getContentAsString());
    }

    @Test
    @WithUserDetails("user")
    void addComment_success() throws Exception{

        Comment c = new Comment();
        c.setName("comentario");
        c.setBody("bodybodybodybody");
        c.setEmail("email");
        c.setId(1L);

        PagedResponse<Comment> comentarios = new PagedResponse();
        comentarios.setContent(List.of(c));

        when(service.addComment(any(CommentRequest.class), any(Long.class), any(UserPrincipal.class))).thenReturn(c);

        mockMvc.perform(post("/api/posts/{postId}/comments", 1L)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(c)))
                //.andExpect(jsonPath("$.id").value(1))
                .andExpect(status().isCreated());
    }

    @Test
    void addComment_unauthorized() throws Exception{

        mockMvc.perform(post("/api/posts/{postId}/comments", 1L))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    void getComment_success() throws Exception{

        Comment c = new Comment();
        c.setName("comentario");
        c.setBody("bodybodybodybody");
        c.setEmail("email");
        c.setId(1L);

        when(service.getComment(any(Long.class), any(Long.class))).thenReturn(c);

        MvcResult result = mockMvc.perform(get("/api/posts/{postId}/comments/{id}", 1L, 1L))
                .andExpect(jsonPath("$.id", is(1)))
                .andReturn();

        log.info(result.getResponse().getContentAsString());
    }

    @Test
    void getComment_NotFound() throws Exception{

        when(service.getComment(any(Long.class), any(Long.class))).thenThrow(ResourceNotFoundException.class);

        MvcResult result = mockMvc.perform(get("/api/posts/{postId}/comments/{id}", 1L, 1L))
                .andExpect(status().isNotFound())
                .andReturn();

        log.info(result.getResponse().getContentAsString());
    }

    @Test
    @WithUserDetails("user")
    void updateComment() throws Exception{

        Comment c = new Comment();
        c.setName("comentario");
        c.setBody("bodybodybodybody");
        c.setEmail("email");
        c.setId(1L);

        PagedResponse<Comment> comentarios = new PagedResponse();
        comentarios.setContent(List.of(c));

        when(service.updateComment(any(Long.class), any(Long.class), any(CommentRequest.class), any(UserPrincipal.class))).thenReturn(c);

        mockMvc.perform(put("/api/posts/{postId}/comments/{id}", 1L, 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(c)))
                //.andExpect(jsonPath("$.id").value(1))
                .andExpect(status().isOk());
    }

    @Test
    void updateComment_unauthorized() throws Exception{

        mockMvc.perform(put("/api/posts/{postId}/comments/{id}", 1L, 1L))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    @WithUserDetails("user")
    void updateComment_badrequest() throws Exception{

        Comment c = new Comment();
        c.setName("comentario");
        c.setBody("bodybodybodybody");
        c.setEmail("email");
        c.setId(1L);

        when(service.updateComment(any(Long.class), any(Long.class), any(CommentRequest.class), any(UserPrincipal.class))).thenThrow(BadRequestException.class);

        MvcResult result = mockMvc.perform(put("/api/posts/{postId}/comments/{id}", 1L, 1L)
                .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andReturn();

        log.info(result.getResponse().getContentAsString());

    }

    @Test
    @WithUserDetails("user")
    void deleteComment_success() throws Exception {

        Post p = new Post();
        p.setId(1L);

        Comment c = new Comment();
        c.setName("comentario");
        c.setBody("bodybodybodybody");
        c.setEmail("email");
        c.setId(1L);
        c.setPost(p);

        when(service.deleteComment(any(Long.class), any(Long.class), any(UserPrincipal.class))).thenReturn(new ApiResponse(Boolean.TRUE, "You successfully deleted comment"));

        mockMvc.perform(delete("/api/posts/{postId}/comments/{id}", 1L, 1L)
                        .contentType("application/json"))
                .andExpect(status().isOk());

    }
}