package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.model.Category;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,classes = {SpringSecurityTestWebConfig.class})
@AutoConfigureMockMvc
class CategoryControllerTest {

    @MockBean
    CategoryServiceImpl categoryService;

    @InjectMocks
    CategoryController categoryController;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    static Long CATEGORY_ID=1L;
    static Category category;
    static UserPrincipal user;
    static ApiResponse apiResponse;

    @BeforeEach
    void init(){
        apiResponse=new ApiResponse(true,"You successfully deleted category");
        user = new UserPrincipal(1L,"user", "user","user","user@gmail.com", "user", List.of(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString())));
        category=new Category();category.setId(1L); category.setName("Viaje");
    }


    @Test
    void getAllCategories_Success() throws Exception{
        PagedResponse pr=new PagedResponse(List.of(category),1,1,1,1,true);
        when(categoryService.getAllCategories(anyInt(),anyInt())).thenReturn(pr);

        mockMvc.perform(get("/api/categories")
                .param("page","1")
                .param("size","1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }


    @Test
    @WithUserDetails("user")
    void addCategory_Success() throws Exception {
        when(categoryService.addCategory(any(Category.class),any(UserPrincipal.class))).thenReturn(category);
        mockMvc.perform(
            post("/api/categories")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(category)))
                //.andExpect(jsonPath("$.id").value(1L))
                .andExpect(status().isCreated());
    }

    @Test
    @WithUserDetails("admin")
    void addCategory_Forbidden() throws Exception {
        mockMvc.perform(
            post("/api/categories")
                .contentType("application/json")
                .content(objectMapper.writeValueAsBytes(category)))
            .andExpect(status().isForbidden());
    }

    @Test
    void addCategory_UnAuthorized() throws Exception {
        mockMvc.perform(
            post("/api/categories")
                .contentType("application/json")
                .content(objectMapper.writeValueAsBytes(category)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void getCategory_Success() throws Exception{
        when(categoryService.getCategory(anyLong())).thenReturn(category);
        mockMvc.perform(get("/api/categories/{id}",CATEGORY_ID))
                .andExpect(jsonPath("$.id").value(CATEGORY_ID))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void updateCategory_Success() throws  Exception{
        when(categoryService.updateCategory(anyLong(),any(Category.class),any(UserPrincipal.class))).thenReturn(category);

        mockMvc.perform(put("/api/categories/{id}",CATEGORY_ID)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isOk());
    }


    @Test
    @WithUserDetails("admin")
    void deleteCategory_Success() throws Exception{

        when(categoryService.deleteCategory(anyLong(),any(UserPrincipal.class))).thenReturn(new ResponseEntity<>(apiResponse, HttpStatus.OK));

        mockMvc.perform(delete("/api/categories/{id}",CATEGORY_ID)                       )
                .andExpect(status().isOk());
    }

    @Test
    void deleteCategory_UnAuthorized() throws Exception{

        mockMvc.perform(delete("/api/categories/{id}",CATEGORY_ID))
                .andExpect(status().isUnauthorized());
    }

}