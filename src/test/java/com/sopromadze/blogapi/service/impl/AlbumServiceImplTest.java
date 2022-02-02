package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.BlogapiException;
import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.AlbumResponse;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.request.AlbumRequest;
import com.sopromadze.blogapi.repository.AlbumRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.stubbing.Answer;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AlbumServiceImplTest {

    @Mock
    AlbumRepository albumRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    AlbumServiceImpl albumService;

    static Long ALBUM_ID=1L;
    static Long USER_ID=1L;
    static Album album;
    static Album album2;
    static User user;
    static User user2;
    static AlbumRequest albumRequest;
    static AlbumResponse albumResponse;
    static UserPrincipal userPrincipalUser;
    static UserPrincipal userPrincipalAdmin;
    static Role adminRole;
    static ApiResponse apiResponse;

    @BeforeEach
    void init(){
        albumResponse=new AlbumResponse();
        user=new User(); user.setId(USER_ID);
        user2=new User(); user2.setId(2L);
        adminRole=new Role(RoleName.ROLE_ADMIN);
        album=new Album();
        album2=new Album();
        albumRequest=new AlbumRequest(); albumRequest.setTitle("Cantando bajo la lluvia");
        userPrincipalUser= new UserPrincipal(USER_ID, "Maria","Lopez", "user","marialopez@gmail.com","user", Collections.singleton(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString())));
        userPrincipalAdmin= new UserPrincipal(USER_ID, "Jose","Ramirez", "admin","joselito@gmail.com","admin", Collections.singleton(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString())));
        apiResponse=new ApiResponse(Boolean.TRUE, "You successfully deleted album");
    }



    @Test
    void addAlbum_Success(){
        album.setUser(user);
        doNothing().when(modelMapper).map(albumRequest,album);
        when(userRepository.getUser(any(UserPrincipal.class))).thenReturn(user);
        when(albumRepository.save(any(Album.class))).thenReturn(album);
        assertNotNull(albumService.addAlbum(albumRequest,userPrincipalUser).getUser());
    }

    @Test
    void updateAlbum_Success(){
        String title= "Cantando bajo la lluvia";
        Album a2 =new Album(); a2.setUser(user); a2.setTitle(title);
        album.setUser(user);

        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(album));
        when(userRepository.getUser(any(UserPrincipal.class))).thenReturn(user);
        when(albumRepository.save(a2)).thenReturn(a2);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Album a1=invocationOnMock.getArgument(1);
                Album a2=invocationOnMock.getArgument(2);
                a2.setTitle(a1.getTitle());
                return null;
            }
        }).when(modelMapper).map(a2,album);

        assertEquals(title, albumService.updateAlbum(ALBUM_ID,albumRequest,userPrincipalAdmin).getTitle());
    }

    @Test
    void getAlbum_Success (){
        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(album));
        assertNotNull(albumService.getAlbum(ALBUM_ID));
    }

    @Test
    void getAlbum_ResourceNotFoundException(){
        when(albumRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,()-> albumService.getAlbum(ALBUM_ID));
    }

    @Test
    void updateAlbum_ResourceNotFoundException(){
        when(albumRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(userRepository.getUser(any(UserPrincipal.class))).thenReturn(user);
        assertThrows(ResourceNotFoundException.class,()-> albumService.updateAlbum(ALBUM_ID,albumRequest,userPrincipalUser));
    }
    @Test
    void updateAlbum_BlogapiException(){
        user.setId(USER_ID);
        album.setUser(user);
        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(album));
        when(userRepository.getUser(any(UserPrincipal.class))).thenReturn(user2);
        assertThrows(BlogapiException.class,()-> albumService.updateAlbum(ALBUM_ID,albumRequest,userPrincipalUser));
    }

    @Test
    void deleteAlbum_Success(){
        user.setId(USER_ID);
        album.setUser(user);
        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(album));
        when(userRepository.getUser(any(UserPrincipal.class))).thenReturn(user);
        doNothing().when(albumRepository).deleteById(anyLong());
        assertEquals(apiResponse,albumService.deleteAlbum(ALBUM_ID,userPrincipalUser));
    }

    @Test
    void deleteAlbum_BlogapiException(){
        user.setId(USER_ID);
        album.setUser(user);
        when(albumRepository.findById(anyLong())).thenReturn(Optional.of(album));
        when(userRepository.getUser(any(UserPrincipal.class))).thenReturn(user2);
        assertThrows(BlogapiException.class,()-> albumService.deleteAlbum(ALBUM_ID,userPrincipalUser));
    }

    @Test
    void deleteAlbum_ResourceNotFoundException(){
        when(albumRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(userRepository.getUser(any(UserPrincipal.class))).thenReturn(user);
        assertThrows(ResourceNotFoundException.class,()-> albumService.deleteAlbum(ALBUM_ID,userPrincipalUser));
    }

    @Test
    void getUserAlbums_Success (){
        List<Album> listAlbums =new ArrayList<>(); listAlbums.add(album);
        Page<Album> pageableAlbum=new PageImpl<Album>(listAlbums);

        when(userRepository.getUserByName(anyString())).thenReturn(user);
        when(albumRepository.findByCreatedBy(anyLong(),any(Pageable.class))).thenReturn(pageableAlbum);

        assertEquals(1,albumService.getUserAlbums("Jacoco",1,1).getSize());
    }

}