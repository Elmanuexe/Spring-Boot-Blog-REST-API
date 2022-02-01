package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.Photo;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.payload.PhotoRequest;
import com.sopromadze.blogapi.payload.PhotoResponse;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.PhotoService;
import lombok.extern.java.Log;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;


@Log
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SpringSecurityTestWebConfig.class})
@AutoConfigureMockMvc
class PhotoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PhotoService photoService;

    @Test
    void getAllPhotosSuccessTest() throws Exception{

        //Instanciamos todo lo necesario

        PhotoResponse photo1 = new PhotoResponse(
                1L,
                "fotito 1",
                "https://google.com",
                "otra url",
                1L
        );

        PhotoResponse photo2 = new PhotoResponse(
                2L,
                "fotito 2",
                "https://google.com",
                "otra url",
                1L
        );

        PhotoResponse photo3 = new PhotoResponse(
                3L,
                "fotito 3",
                "https://google.com",
                "otra url",
                1L
        );


        List<PhotoResponse> photos = List.of(photo1, photo2, photo3);
        PagedResponse<PhotoResponse> pagedResponse = new PagedResponse<>(photos, 1, 1, 3, 1, true);

        //Mockeamos lo necesario

        Mockito.when(photoService.getAllPhotos(1,1)).thenReturn(pagedResponse);

        //Implementamos el test

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/photos")
                        .param("page", "1")
                        .param("size", "1"))
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();

        Assertions.assertThat(result).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(pagedResponse));

    }

    @Test
    @WithUserDetails("user")
    void addPhotoSuccesTest() throws Exception{

        //Instanciamos lo necesario

        PhotoRequest request = new PhotoRequest();
        request.setTitle("Photo 1");
        request.setUrl("https://www.google.com");
        request.setThumbnailUrl("https://www.google.com");

        UserPrincipal user = new UserPrincipal(1L,
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user",
                Collections.singleton(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString())));

        Album album = new Album();
        album.setTitle("Vacaciones");

        Photo photo = new Photo(
                "Photo 1",
                "https://www.google.com",
                "https://www.google.com",
                album
        );
        photo.setId(1L);

        //Mockeamos lo necesario

        Mockito.when(photoService.addPhoto(request, user)).thenReturn(photo);

        //Implementamos el test

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/photos")
                .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();
        Assertions.assertThat(result).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(photo));

    }

    @Test
    void getPhoto() {
    }

    @Test
    void updatePhoto() {
    }

    @Test
    void deletePhoto() {
    }
}