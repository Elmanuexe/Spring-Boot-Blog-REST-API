package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.payload.UserIdentityAvailability;
import com.sopromadze.blogapi.payload.UserProfile;
import com.sopromadze.blogapi.payload.UserSummary;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.AlbumService;
import com.sopromadze.blogapi.service.PostService;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @MockBean
    private PostService postService;

    @MockBean
    private AlbumService albumService;

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
    void checkUsernameAvailabilitySuccessTest() throws Exception{

        //Instanciamos lo necesario

        UserIdentityAvailability userIdentityAvailability = new UserIdentityAvailability(true);

        //Mockeamos lo necesario

        Mockito.when(userService.checkUsernameAvailability("user")).thenReturn(userIdentityAvailability);

        //Implementamos el test

        MvcResult  mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/users/checkUsernameAvailability")
                .param("username", "user"))
                .andExpect(status().isOk())
                .andReturn();

    }

    @Test
    void checkUsernameAvailabilityBadRequestExceptionTest() throws Exception{

        //Instanciamos lo necesario

        UserIdentityAvailability userIdentityAvailability = new UserIdentityAvailability(true);

        //Mockeamos lo necesario

        Mockito.when(userService.checkUsernameAvailability("user")).thenReturn(userIdentityAvailability);

        //Implementamos el test

        MvcResult  mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/users/checkUsernameAvailability")
                        .param("user", "user"))
                .andExpect(status().isBadRequest())
                .andReturn();

    }

    @Test
    void checkEmailAvailabilitySuccessTest() throws Exception{

        //Instanciamos lo necesario

        UserIdentityAvailability userIdentityAvailability = new UserIdentityAvailability(true);

        //Mockeamos lo necesario

        Mockito.when(userService.checkEmailAvailability(any(String.class))).thenReturn(userIdentityAvailability);

        //Implementamos el test

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/users/checkEmailAvailability")
                .param("email", "luismimaquina@gmail.com"))
                .andExpect(status().isOk())
                .andReturn();

    }

    @Test
    void checkEmailAvailabilityBadRequestTest() throws Exception{

        //Instanciamos lo necesario

        UserIdentityAvailability userIdentityAvailability = new UserIdentityAvailability(true);

        //Mockeamos lo necesario

        Mockito.when(userService.checkEmailAvailability(any(String.class))).thenReturn(userIdentityAvailability);

        //Implementamos el test

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/users/checkEmailAvailability")
                        .param("user", "luismimaquina@gmail.com"))
                .andExpect(status().isBadRequest())
                .andReturn();

    }

    @Test
    void getUSerProfileSuccessTest() throws Exception{

        //Instanciamos lo necesario

        UserProfile userProfile = new UserProfile();

        //Mockeamos lop necesario

        Mockito.when(userService.getUserProfile(any(String.class))).thenReturn(userProfile);

        //Implementamos el test

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/users//{username}/profile", "user"))
                .andExpect(status().isOk())
                .andReturn();

    }

    @Test
    void getPostsCreatedBySuccessTest() throws Exception{

        //Instanciamos lo necesario

        PagedResponse<Post> response = new PagedResponse<>();

        MultiValueMap<String, String> expected = new LinkedMultiValueMap<>();
        expected.add("page", "0");
        expected.add("size", "1");


        //Mockeamos lo necesario

        Mockito.when(postService.getPostsByCreatedBy(any(String.class), any(Integer.class), any(Integer.class)))
                .thenReturn(response);

        //Implementamos el test

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{username}/posts", "user")
                .params(expected))
                .andExpect(status().isOk())
                .andReturn();

    }

    @Test
    void getUserAlbumsSuccessTest() throws Exception{

        //Instanciamos lo necesario

        PagedResponse<Album> response = new PagedResponse<>();

        //Mockeamos lo necesario

        Mockito.when(albumService.getUserAlbums(any(String.class), any(Integer.class), any(Integer.class)))
                .thenReturn(response);

        //Implementamos el test

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{username}/albums", "user"))
                .andExpect(status().isOk())
                .andReturn();

    }

    @Test
    @WithUserDetails("admin")
    void addUserSuccessTest() throws Exception{

        //Instanciamos lo necesario

        User user = new User(
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user"
        );


        ResponseEntity<User> response = new ResponseEntity< >(user, HttpStatus.CREATED);

        //Mockeamos lo necesario

        Mockito.when(userService.addUser(any(User.class))).thenReturn(user);

        //Implementamos el test

        MvcResult mvcResult = mockMvc.perform(post("/api/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

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