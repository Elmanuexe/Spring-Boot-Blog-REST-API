package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.exception.UnauthorizedException;
import com.sopromadze.blogapi.model.Category;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.Tag;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.PostRequest;
import com.sopromadze.blogapi.repository.CategoryRepository;
import com.sopromadze.blogapi.repository.PostRepository;
import com.sopromadze.blogapi.repository.TagRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    PostRepository postRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    TagRepository tagRepository;

    @InjectMocks
    PostServiceImpl postService;

    static Long ONE_ID=1L;
    static int ONE =1;
    static Pageable pageable;
    static Page <Post> page;
    static Post post;
    static User user;
    static Category category;
    static Tag tag;
    static UserPrincipal userPrincipalUser,userPrincipalAdmin;

    @BeforeEach
    void init(){
        pageable = PageRequest.of(ONE, ONE, Sort.Direction.DESC,"a");
        user= new User();user.setFirstName("Paco");user.setId(ONE_ID);
        post=new Post();post.setUser(user);
        page= new PageImpl(List.of(post),pageable, ONE);
        tag=new Tag();tag.setName("Super tag");tag.setId(ONE_ID);tag.setPosts(List.of(post));
        userPrincipalUser= new UserPrincipal(2L, "Maria","Lopez", "user","marialopez@gmail.com","user", List.of(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString())));
        userPrincipalAdmin= new UserPrincipal(ONE_ID, "Jose","Ramirez", "admin","joselito@gmail.com","admin", List.of(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString())));
        category=new Category("Juego");category.setId(ONE_ID);
    }

    @Test
    void getAllPosts_Success (){
        lenient().when(postRepository.findAll(any(Pageable.class))).thenReturn(page);

        assertNotNull(postService.getAllPosts(ONE, ONE));
        assertEquals(List.of(post),postService.getAllPosts(ONE, ONE).getContent());
    }

    @Test
    void getPostsByCreatedBy_Success (){

        lenient().when(userRepository.getUserByName(anyString())).thenReturn(user);
        lenient().when(postRepository.findByCreatedBy(anyLong(),any(Pageable.class))).thenReturn(page);

        assertNotNull(postService.getPostsByCreatedBy("Paco", ONE, ONE));
        assertEquals(1,postService.getPostsByCreatedBy("Paco" , ONE, ONE).getSize());
    }

    @Test
    void getPostsByCategory_Success (){

        lenient().when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        lenient().when(postRepository.findByCategory(anyLong(),any(Pageable.class))).thenReturn(page);

        assertNotNull(postService.getPostsByCategory(ONE_ID, ONE, ONE));
        assertEquals(1,postService.getPostsByCategory(ONE_ID, ONE, ONE).getContent().size());
    }

    @Test
    void getPostsByCategory_ResourceNotFoundException(){

        lenient().when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,()->postService.getPostsByCategory(ONE_ID, ONE, ONE));
    }

    @Test
    void getPostsByTag_Success (){

        lenient().when(tagRepository.findById(anyLong())).thenReturn(Optional.of(tag));
        lenient().when(postRepository.findByTagsIn(anyList(),any(Pageable.class))).thenReturn(page);

        assertNotNull(postService.getPostsByTag(ONE_ID, ONE, ONE));
        assertEquals(1,postService.getPostsByTag(ONE_ID, ONE, ONE).getContent().size());
    }

    @Test
    void getPostsByTag_ResourceNotFoundException(){

        lenient().when(tagRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,()->postService.getPostsByTag(ONE_ID, ONE, ONE));
    }

    @Test
    void updatePost_Success(){

        PostRequest dto=new PostRequest(); dto.setCategoryId(ONE_ID); dto.setTitle("Party");dto.setBody("En CasaBlanca");
        lenient().when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        lenient().when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        lenient().when(postRepository.save(post)).thenReturn(post);

        assertNotNull(postService.updatePost(ONE_ID,dto,userPrincipalAdmin));
        assertEquals("Party",postService.updatePost(ONE_ID,dto,userPrincipalAdmin).getTitle());
    }

    @Test
    void updatePost_ResourceNotFoundException_Post(){
        PostRequest dto=new PostRequest(); dto.setCategoryId(ONE_ID); dto.setTitle("Party");dto.setBody("En CasaBlanca");
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,()->postService.updatePost(ONE_ID,dto,userPrincipalAdmin));
    }

    @Test
    void updatePost_ResourceNotFoundException_Category(){
        PostRequest dto=new PostRequest(); dto.setCategoryId(ONE_ID); dto.setTitle("Party");dto.setBody("En CasaBlanca");
        lenient().when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());
        lenient().when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        assertThrows(ResourceNotFoundException.class,()->postService.updatePost(ONE_ID,dto,userPrincipalAdmin));
    }

    @Test
    void updatePost_UnauthorizedException(){
        PostRequest dto=new PostRequest(); dto.setCategoryId(ONE_ID); dto.setTitle("Party");dto.setBody("En CasaBlanca");

        lenient().when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        lenient().when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));


        assertThrows(UnauthorizedException.class,()->postService.updatePost(ONE_ID,dto,userPrincipalUser));
    }

    @Test
    void deletePost_Success(){
        lenient().when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        doNothing().when(postRepository).deleteById(anyLong());

        assertNotNull(postService.deletePost(ONE_ID,userPrincipalAdmin));
        assertEquals("You successfully deleted post",postService.deletePost(ONE_ID,userPrincipalAdmin).getMessage());
    }

    @Test
    void deletePost_ResourceNotFoundException(){
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,()-> postService.deletePost(ONE_ID,userPrincipalAdmin));
    }

    @Test
    void deletePost_UnauthorizedException(){
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        assertThrows(UnauthorizedException.class,()-> postService.deletePost(ONE_ID,userPrincipalUser));
    }

    @Test
    void addPost_Success (){
        Tag tag2=new Tag(); tag2.setName("TagName");tag2.setId(ONE_ID);tag2.setPosts(List.of(new Post()));
        User user2= new User();user2.setFirstName("Paco");user.setId(ONE_ID);
        Category category2=new Category(); category2.setName("Zancos");category2.setId(ONE_ID);
        Post post1= new Post();post1.setUser(user2);post1.setCategory(category2);post1.setTags(List.of(tag2));
        PostRequest dto=new PostRequest(); dto.setCategoryId(ONE_ID); dto.setTitle("Party");dto.setBody("En CasaBlanca");

        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        lenient().when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category2));
        lenient().when(tagRepository.findByName(anyString())).thenReturn(tag2);
        lenient().when(tagRepository.save(any(Tag.class))).thenReturn(tag2);
        lenient().when(postRepository.save(any(Post.class))).thenReturn(post1);

        assertNotNull(postService.addPost(dto,userPrincipalAdmin));
        assertEquals("Zancos",postService.addPost(dto,userPrincipalAdmin).getCategory());
    }

    @Test
    void addPost_ResourceNotFoundException_User (){
        PostRequest dto=new PostRequest(); dto.setCategoryId(ONE_ID); dto.setTitle("Party");dto.setBody("En CasaBlanca");
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,()->postService.addPost(dto,userPrincipalAdmin));
    }

    @Test
    void addPost_ResourceNotFoundException_Category(){
        User user2= new User();user2.setFirstName("Paco");user.setId(ONE_ID);
        Category category2=new Category(); category2.setName("Zancos");category2.setId(ONE_ID);
        PostRequest dto=new PostRequest(); dto.setCategoryId(ONE_ID); dto.setTitle("Party");dto.setBody("En CasaBlanca");

        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        lenient().when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,()->postService.addPost(dto,userPrincipalAdmin));
    }

    @Test
    void getPost_Success(){
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        assertEquals(post,postService.getPost(ONE_ID));
    }

    @Test
    void getPost_ResourceNotFoundException(){
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,()->postService.getPost(ONE_ID));
    }




}