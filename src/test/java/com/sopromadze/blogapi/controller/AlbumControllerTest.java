package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.Photo;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.payload.AlbumResponse;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.payload.PhotoResponse;
import com.sopromadze.blogapi.payload.request.AlbumRequest;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.AlbumService;
import com.sopromadze.blogapi.service.PhotoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.sopromadze.blogapi.model.user.User;

import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SpringSecurityTestWebConfig.class})
@AutoConfigureMockMvc
class AlbumControllerTest {

    @MockBean
    private AlbumService albumService;

    @MockBean
    private PhotoService photoService;

    @InjectMocks
    private AlbumController albumController;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static User usuario;
    private static Photo photo1, photo2;
    private static List<Photo> photos;
    private static Album album;
    private static AlbumRequest albumRequest;
    private static UserPrincipal user;
    private static AlbumResponse albumResponse;
    private static PhotoResponse photoResponse;

    @BeforeEach
    void initTest(){

        user = new UserPrincipal(
                123L,
                "Paco",
                "Lopez",
                "pacols",
                "pacols@gmail.com",
                "123",
                Collections.singleton(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString())));

        usuario = new User();
        usuario.setId(9652L);
        usuario.setUsername("pacols");
        usuario.setEmail("pacosl@gmail.com");


        photo1 = new Photo();
        photo1.setTitle("Foto1");
        photo1.setId(1L);
        photo1.setUrl("www.photo/foto.1.jpg");

        photo2 = new Photo();
        photo1.setTitle("Foto2");
        photo1.setId(1L);
        photo1.setUrl("www.photo/foto.2.jpg");

        photos = new ArrayList<Photo>();
        photos.add(photo1);
        photos.add(photo2);

        photoResponse = new PhotoResponse(2L,"Photo", "www.photo.com", "www.img.com", 95L);


        album = new Album();
        album.setId(95L);
        album.setTitle("Album 1");
        album.setPhoto(photos);
        album.setUser(usuario);

        albumResponse = new AlbumResponse();
        albumResponse.setId(95L);
        albumResponse.setTitle("Album 1");
        albumResponse.setPhoto(photos);
        albumResponse.setUser(usuario);

        albumRequest = new AlbumRequest();
        albumRequest.setId(95L);
        albumRequest.setTitle("Album 1");
        albumRequest.setPhoto(photos);

    }

    @Test
    @WithUserDetails("user")
    void addAlbum_Success() throws Exception{
        when(albumService.addAlbum(albumRequest, user)).thenReturn(album);

        mockMvc.perform(
                post("/api/albums")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(album)))
                        .andExpect(status().isCreated());
    }

    @Test
    @WithUserDetails("admin")
    void addAlbum_Forbiden() throws Exception{
        mockMvc.perform(
                post("/api/albums")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(album)))
                        .andExpect(status().isForbidden());
    }

    @Test
    void getAlbum_Success() throws Exception{
        when(albumService.getAlbum(95L)).thenReturn(album);

        mockMvc.perform(
                get("/api/albums/{id}", 95L)
                        .contentType("application/json"))
                        .andExpect(jsonPath("$.id").value(95L))
                        .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void updateAlbum_Success() throws Exception{
        when(albumService.updateAlbum(5L,albumRequest, user)).thenReturn(albumResponse);

        mockMvc.perform(
                put("/api/albums/{id}",95L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(albumRequest)))
                        .andExpect(status().isCreated());
    }

    @Test
    void updateAlbum_Unauthorized() throws Exception{
        when(albumService.updateAlbum(5L,albumRequest, user)).thenReturn(albumResponse);

        mockMvc.perform(
                put("/api/albums/{id}",95L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(albumRequest)))
                        .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails("admin")
    void deleteAlbum_Success() throws Exception{
        ApiResponse apiResponse = new ApiResponse(true, "Se ha borrado el album");
        when(albumService.deleteAlbum(2L, user)).thenReturn(apiResponse);

        mockMvc.perform(
                delete("/api/albums/{id}", 95L))
                .andExpect(status().isOk());
    }

    @Test
    void deleteAlbum_Unauthorized() throws Exception{
        ApiResponse apiResponse = new ApiResponse(true, "Se ha borrado el album");
        when(albumService.deleteAlbum(2L, user)).thenReturn(apiResponse);

        mockMvc.perform(
                delete("/api/albums/{id}", 95L))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void getAllPhotosByAlbum_Success() throws Exception{
        PagedResponse<PhotoResponse> response =
                new PagedResponse(List.of(photoResponse),1,1,1,1, true);
        when(photoService.getAllPhotosByAlbum(95L,1,1)).thenReturn(response);

        mockMvc.perform(
                get("/api/albums/{id}/photos",95L)
                        .param("page", "1")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(2));
    }
}