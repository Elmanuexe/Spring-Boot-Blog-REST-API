package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.Tag;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,classes = {SpringSecurityTestWebConfig.class})
@AutoConfigureMockMvc
class TagControllerTest {

    @MockBean
    TagService tagService;

    @InjectMocks
    TagController tagController;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    static int ONE=1;
    static Long ONE_ID=1L;
    static Page page;
    static Pageable pageable;
    static Tag tag;
    static PagedResponse pagedResponse;

    @BeforeEach
    void init (){
        tag=new Tag(); tag.setId(ONE_ID); tag.setName("MegaTag"); tag.setPosts(List.of(new Post()));
        pageable= PageRequest.of(ONE, ONE, Sort.Direction.DESC,"a");
        page=new PageImpl(List.of(tag),pageable,ONE);
        pagedResponse=new PagedResponse(); pagedResponse.setPage(ONE);pagedResponse.setContent(List.of(tag));pagedResponse.setLast(true);pagedResponse.setTotalPages(ONE);pagedResponse.setTotalElements(1);
    }

    @Test
    void getAllTags_Success () throws Exception{
        when(tagService.getAllTags(ONE,ONE)).thenReturn(pagedResponse);

        mockMvc.perform(get("/api/tags")
                .param("page","1")
                .param("size","1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(ONE_ID))
                .andExpect(jsonPath("$.content[0].name").value("MegaTag"));
    }

    @Test
    @WithUserDetails("user")
    void addTag_Success () throws Exception {
        when(tagService.addTag(any(Tag.class),any(UserPrincipal.class))).thenReturn(tag);

        mockMvc.perform(post("/api/tags")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(tag)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithUserDetails("admin")
    void addTag_Forbidden() throws Exception {
        mockMvc.perform(post("/api/tags")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(tag)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getTag_Success() throws Exception {
        when(tagService.getTag(anyLong())).thenReturn(tag);

        mockMvc.perform(get("/api/tags/{id}",ONE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ONE_ID))
                .andExpect(jsonPath("$.name").value("MegaTag"));
    }

    @Test
    @WithUserDetails("admin")
    void updateTag_Success () throws Exception{
        when(tagService.updateTag(anyLong(),any(Tag.class),any(UserPrincipal.class))).thenReturn(tag);

        mockMvc.perform(put("/api/tags/{id}",ONE_ID)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(tag)))
                .andExpect(status().isOk());
    }

    @Test
    void updateTag_UnAuthorized () throws Exception{
        mockMvc.perform(put("/api/tags/{id}",ONE_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(tag)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails("user")
    void deleteTag_Success () throws Exception{
        ApiResponse apiResponse=new ApiResponse(); apiResponse.setMessage("Deleted");
        when(tagService.deleteTag(anyLong(),any(UserPrincipal.class))).thenReturn(apiResponse);

        mockMvc.perform(delete("/api/tags/{id}",ONE_ID))
                .andExpect(status().isOk());
    }

    @Test
    void deleteTag_UnAuthorized () throws Exception{
        mockMvc.perform(delete("/api/tags/{id}",ONE_ID))
                .andExpect(status().isUnauthorized());
    }



}