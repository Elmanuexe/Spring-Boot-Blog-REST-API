package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.payload.UserSummary;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SpringSecurityTestWebConfig.class})
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @WithUserDetails("user")
    @Test
    void getCurrentUserSuccessTest() throws Exception{

        //Instanciamos lo necesario

        UserSummary userSummary = new UserSummary(
                1L,
                "user",
                "user",
                "user"
        );

        ResponseEntity<UserSummary> expected = new ResponseEntity< >(userSummary, HttpStatus.OK);

        //Mockeamos lo necesario

        Mockito.when(userService.getCurrentUser(any(UserPrincipal.class))).thenReturn(userSummary);

        //Implementamos el test

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/users/me"))
                .andExpect(status().isOk())
                .andReturn();

    }

    @Test
    void getCurrentUserUnauthorizedTest() throws Exception{

        //Instanciamos lo necesario

        UserSummary userSummary = new UserSummary(
                1L,
                "user",
                "user",
                "user"
        );

        ResponseEntity<UserSummary> expected = new ResponseEntity< >(userSummary, HttpStatus.OK);

        //Mockeamos lo necesario

        Mockito.when(userService.getCurrentUser(any(UserPrincipal.class))).thenReturn(userSummary);

        //Implementamos el test

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/users/me"))
                .andExpect(status().isUnauthorized())
                .andReturn();

    }

    @Test
    void checkUsernameAvailability() {
    }

    @Test
    void checkEmailAvailability() {
    }

    @Test
    void getUSerProfile() {
    }

    @Test
    void getPostsCreatedBy() {
    }

    @Test
    void getUserAlbums() {
    }

    @Test
    void addUser() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void giveAdmin() {
    }

    @Test
    void takeAdmin() {
    }

    @Test
    void setAddress() {
    }
}