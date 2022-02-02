package com.sopromadze.blogapi.service.impl;
import com.sopromadze.blogapi.exception.BlogapiException;
import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.Comment;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.CommentRequest;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.repository.CommentRepository;
import com.sopromadze.blogapi.repository.PostRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CommentServiceImplTest {

    private static final Long POST_ID = 1234L;
    private static final Long COMMENT_ID = 456L;
    private static final Long USER_ID = 352L;
    private static final int PAGE = 1;
    private static final int SIZE = 1;
    private static final String BODY = "Hola que hace";

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
        //Dato de entrada Long id Post, dato de salida PagedResponse con los comentarios
    void getAllComments_Success() {
        Pageable pageable = getPageable();
        when(commentRepository.findByPostId(POST_ID,pageable)).thenReturn(getComments());

        PagedResponse<Comment> response = commentService.getAllComments(POST_ID, PAGE, SIZE);
        assertEquals(1,response.getTotalElements());
        /*List<Long> idP = (List<Long>) response.getContent().stream().map(p-> p.getPost().getId());
        assertTrue(idP.contains(POST_ID));*/
    }


    private Page<Comment> getComments() {
        return new PageImpl<Comment>(Collections.singletonList(getCommentEntity()));
    }

    private User getUser(){
        User user = new User();
        user.setUsername("Pepe");
        user.setLastName("Palotes");
        user.setId(USER_ID);

        return user;
    }

    private Pageable getPageable() {
        return PageRequest.of(PAGE,SIZE, Sort.Direction.DESC, "createdAt");
    }

    @Test
    //Dato de entrada Commentario, dato de salida comentario guardado
    void addComment_Success() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(getPost()));
        when(userRepository.getUser(getUserPrincipal())).thenReturn(getUser());
        Comment commentSave = getCommentEntity();
        commentSave.setId(null);
        when(commentRepository.save(commentSave)).thenReturn(getCommentEntity());

        Comment comment = commentService.addComment(getCommentRequest(), POST_ID, getUserPrincipal());
        assertEquals(COMMENT_ID, comment.getId());
    }

    @Test
    //Dato de entrada Id de un post que no existe, dato de salida ResourceNotFoundException
    void addtComment_NotFound(){
        when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, ()-> commentService.addComment(getCommentRequest(), POST_ID, getUserPrincipal()));
    }

    @Test
    //Dato de entrada id del post y el id del comentario, dato de salida el comentario
    void getComment_Success() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(getPost()));
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(getCommentEntity()));

        Comment comment = commentService.getComment(POST_ID, COMMENT_ID);
        assertEquals(POST_ID, comment.getPost().getId());
    }

    @Test
    //Dato de entrada id de un post que no existe, dato de salida ResourceNotFoundException
    void getComment_NotFound(){
        when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, ()-> commentService.getComment(POST_ID, COMMENT_ID));
    }

    @Test
    //Dato de entrada id de post, id de un comentario que no existe, dato de salida ResourceNotFoundException
    void getComment_NotFoundComment(){
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.getComment(POST_ID,COMMENT_ID));
    }

    @Test
    //Dato de entrada id del post diferente a la id del post del comentario, dato de salida BlogapiException
    void getComment_BlogApi(){
        getCommentEntity().getPost().setId(777L);
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(getPost()));
        getCommentEntity().getPost().setId(78L);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(getCommentEntity()));

        assertThrows(BlogapiException.class, ()-> commentService.getComment(POST_ID,COMMENT_ID));
    }

    @Test
    void updateComment_Success() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(getPost()));
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(getCommentEntity()));
        when(commentRepository.save(getCommentEntity())).thenReturn(getCommentEntity());

        Comment comment = commentService.updateComment(POST_ID, COMMENT_ID, getCommentRequest(), getUserPrincipal());
        assertEquals(COMMENT_ID, comment.getId());
    }

   @Test
   //Dato de entrada id del usuario diferente a la id del usuario logeado, dato de salida BlogapiException
    void updateComment_NotPermission(){
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(getPost()));
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(getCommentEntity()));
        getCommentEntity().getUser().setId(98L);

        assertThrows(BlogapiException.class, () -> commentService.updateComment(POST_ID, COMMENT_ID, getCommentRequest(), getUserRoleUser()),
                "YOU_DON_T_HAVE_PERMISSION_TO + \"update\" + THIS_COMMENT");
    }

    @Test
    //Dato de entrada id del post diferente a la id por parámetro, datos de salida BlogapiException
    void updateComment_BadRequest(){
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(getPost()));
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(getCommentEntity()));
        getPost().setId(4586L);

        assertThrows(BlogapiException.class, ()-> commentService.updateComment(POST_ID, COMMENT_ID, getCommentRequest(), getUserRoleUser()),
                "BAD_REQUEST, COMMENT_DOES_NOT_BELONG_TO_POST");
    }

    @Test()
    //Dato de entrada id de un post que no existe, datos de salida ResourceNotFoundException
    void updateComment_NotFound(){
        when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.updateComment(POST_ID, COMMENT_ID, getCommentRequest(), getUserPrincipal()),
                "POST_STR, ID_STR, postId");
    }

    @Test()
    //Dato de entrada id de un comentario que no existe, datos de salida ResourceNotFoundException
    void updateComment_NotFoundComment(){
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(getPost()));
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.updateComment(POST_ID, COMMENT_ID, getCommentRequest(), getUserPrincipal()),
                "COMMENT_STR, ID_STR, id");
    }

    private UserPrincipal getUserPrincipal() {
        UserPrincipal userPrincipal = new UserPrincipal(USER_ID, "Pepe","Palote", "pepalote","pepalote@gmail.com","1234", Collections.singleton(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString())));
        return userPrincipal;
    }

    private UserPrincipal getUserRoleUser(){
        UserPrincipal userPrincipal = new UserPrincipal(1655L, "Pepe","Palote", "pepalote","pepalote@gmail.com","1234", Collections.singleton(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString())));;
        return userPrincipal;
    }

    private Comment getCommentEntity() {
        Comment comment = new Comment();
        comment.setName("pepalote");
        comment.setEmail("pepalote@gmail.com");
        comment.setUser(getUser());
        comment.setPost(getPost());
        comment.setId(COMMENT_ID);
        comment.setBody(BODY);
        return comment;
    }

    private CommentRequest getCommentRequest(){
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setBody(BODY);
        return commentRequest;
    }

    private Post getPost() {
        Post post = new Post();
        post.setUser(getUser());
        post.setId(POST_ID);
        return post;
    }

    @Test
    //Dato de entrada id comentario, dato de salida ApiResponse (True)
    void deleteComment_Success() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(getPost()));
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(getCommentEntity()));

        ApiResponse apiResponse = commentService.deleteComment(POST_ID, COMMENT_ID, getUserPrincipal());
        verify(commentRepository).deleteById(COMMENT_ID);
        assertTrue(apiResponse.getSuccess(), "You successfully deleted comment");
    }

    @Test()
    //Dato de entrada id de post que no existe, dato de salida ResourceNotFoundException
    void deleteComment_NotFound(){
        when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.deleteComment(POST_ID, COMMENT_ID, getUserPrincipal()),
                "POST_STR, ID_STR, postId");
    }

    @Test
    //Dato de entrada id de comentario que no existe, dato de salida ResourceNotFoundException
    void deleteComment_NotFoundComment(){
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(getPost()));
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, ()-> commentService.deleteComment(POST_ID, COMMENT_ID, getUserPrincipal()),
                "COMMENT_STR, ID_STR, id");
    }

    @Test
    //Dato de entrada usuario no autorizado, dato de salida BlogapiException
    void deleteComment_NotPermission(){
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(getPost()));
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(getCommentEntity()));

        getCommentEntity().getUser().setId(98L);
        assertThrows(BlogapiException.class, () -> commentService.deleteComment(POST_ID, COMMENT_ID, getUserRoleUser()),
                "YOU_DON_T_HAVE_PERMISSION_TO + \"delete\" + THIS_COMMENT");
    }

    @Test
    //Dato de entrada id del post diferente a la id del post del comentario, datos de salida ApiResponse (False)
    void deleteComment_False(){
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(getPost()));
        getPost().setId(65983L);

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(getCommentEntity()));
        ApiResponse apiResponse  = commentService.deleteComment(POST_ID, COMMENT_ID, getUserPrincipal());
        assertFalse(!apiResponse.getSuccess(),"COMMENT_DOES_NOT_BELONG_TO_POST");
    }
}