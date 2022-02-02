package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.BadRequestException;
import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.exception.UnauthorizedException;
import com.sopromadze.blogapi.model.Todo;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.repository.TodoRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TodoServiceImplTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TodoServiceImpl todoService;

    @Test
    void completeTodoSuccessTest() {

        //Instanciamos lo necesario

        UserPrincipal userPrincipal = new UserPrincipal(
                1L,
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user",
                Collections.singleton(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString()))
        );

        User user = new User(
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user"
        );
        user.setId(1L);

        Todo todo = new Todo();
        todo.setUser(user);

        //Mockeamos lo necesario

        Mockito.when(todoRepository.findById(any(Long.class))).thenReturn(Optional.of(todo));
        Mockito.when(userRepository.getUser(any(UserPrincipal.class))).thenReturn(user);
        Mockito.when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        //Implementamos el test

        Assertions.assertEquals(todo, todoService.completeTodo(1L, userPrincipal));

    }

    @Test
    void completeTodoResourceNotFoundExceptionTest() {

        //Instanciamos lo necesario

        UserPrincipal userPrincipal = new UserPrincipal(
                1L,
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user",
                Collections.singleton(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString()))
        );

        //Mockeamos lo necesario

        Mockito.when(todoRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        //Implementamos el test

        Assertions.assertThrows(ResourceNotFoundException.class, () -> todoService.completeTodo(1L, userPrincipal));

    }

    @Test
    void completeTodoUnauthorizedExceptionTest() {

        //Instanciamos lo necesario

        UserPrincipal userPrincipal = new UserPrincipal(
                1L,
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user",
                Collections.singleton(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString()))
        );

        User user1 = new User(
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user"
        );
        user1.setId(1L);

        User user2 = new User(
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user"
        );
        user2.setId(2L);

        Todo todo = new Todo();
        todo.setUser(user2);

        //Mockeamos lo necesario

        Mockito.when(todoRepository.findById(any(Long.class))).thenReturn(Optional.of(todo));
        Mockito.when(userRepository.getUser(any(UserPrincipal.class))).thenReturn(user1);

        //Implementamos el test

        Assertions.assertThrows(UnauthorizedException.class, () -> todoService.completeTodo(1L, userPrincipal));

    }

    @Test
    void unCompleteTodoSuccessTest() {

        //Instanciamos lo necesario

        UserPrincipal userPrincipal = new UserPrincipal(
                1L,
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user",
                Collections.singleton(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString()))
        );

        User user = new User(
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user"
        );
        user.setId(1L);

        Todo todo = new Todo();
        todo.setUser(user);

        //Mockeamos lo necesario

        Mockito.when(todoRepository.findById(any(Long.class))).thenReturn(Optional.of(todo));
        Mockito.when(userRepository.getUser(any(UserPrincipal.class))).thenReturn(user);
        Mockito.when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        //Implementamos el test

        Assertions.assertEquals(todo, todoService.unCompleteTodo(1L, userPrincipal));

    }

    @Test
    void unCompleteTodoResourceNotFoundExceptionTest() {

        //Instanciamos lo necesario

        UserPrincipal userPrincipal = new UserPrincipal(
                1L,
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user",
                Collections.singleton(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString()))
        );

        //Mockeamos lo necesario

        Mockito.when(todoRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        //Implementamos el test

        Assertions.assertThrows(ResourceNotFoundException.class, () -> todoService.unCompleteTodo(1L, userPrincipal));

    }

    @Test
    void unCompleteTodoUnauthorizedExceptionTest() {

        //Instanciamos lo necesario

        UserPrincipal userPrincipal = new UserPrincipal(
                1L,
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user",
                Collections.singleton(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString()))
        );

        User user1 = new User(
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user"
        );
        user1.setId(1L);

        User user2 = new User(
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user"
        );
        user2.setId(2L);

        Todo todo = new Todo();
        todo.setUser(user2);

        //Mockeamos lo necesario

        Mockito.when(todoRepository.findById(any(Long.class))).thenReturn(Optional.of(todo));
        Mockito.when(userRepository.getUser(any(UserPrincipal.class))).thenReturn(user1);

        //Implementamos el test

        Assertions.assertThrows(UnauthorizedException.class, () -> todoService.unCompleteTodo(1L, userPrincipal));

    }

    @Test
    void getAllTodosSuccessTest() {

        //Instanciamos lo necesario

        UserPrincipal userPrincipal = new UserPrincipal(
                1L,
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user",
                Collections.singleton(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString()))
        );

        Todo todo1 = new Todo();
        Todo todo2 = new Todo();
        Todo todo3 = new Todo();
        List<Todo> lista = List.of(todo1, todo2, todo3);

        Page<Todo> todos = new PageImpl<Todo>(lista);

        PagedResponse<Todo> result= new PagedResponse<>(lista, todos.getNumber(), todos.getSize(), todos.getTotalElements(),
                todos.getTotalPages(), todos.isLast());

        //Mockeamos lo necesario

        Mockito.when(todoRepository.findByCreatedBy(any(Long.class), any(Pageable.class))).thenReturn(todos);

        //Implementamos el test

        Assertions.assertEquals(result, todoService.getAllTodos(userPrincipal, 0, 1));

    }

    @Test
    void getAllTodosBadRequestExceptionTest() {

        //Instanciamos lo necesario

        UserPrincipal userPrincipal = new UserPrincipal(
                1L,
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user",
                Collections.singleton(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString()))
        );

        //Implementamos los test

        Assertions.assertAll(
                () -> Assertions.assertThrows(BadRequestException.class, () -> todoService.getAllTodos(userPrincipal, -1, 1)),
                () -> Assertions.assertThrows(BadRequestException.class, () -> todoService.getAllTodos(userPrincipal, 0, -1)),
                () -> Assertions.assertThrows(BadRequestException.class, () -> todoService.getAllTodos(userPrincipal, 1, 35))
        );

    }



    @Test
    void addTodo() {

        //Instanciamos lo necesario

        UserPrincipal userPrincipal = new UserPrincipal(
                1L,
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user",
                Collections.singleton(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString()))
        );

        User user = new User(
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user"
        );
        user.setId(1L);

        Todo todo = new Todo();
        todo.setUser(user);

        //Mockeamos lo necesario

        Mockito.when(userRepository.getUser(any(UserPrincipal.class))).thenReturn(user);
        Mockito.when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        //Implementamos el test

        Assertions.assertEquals(todo,  todoService.addTodo(todo, userPrincipal));

    }

    @Test
    void getTodoSuccessTest() {

        //Instanciamos lo necesario

        UserPrincipal userPrincipal = new UserPrincipal(
                1L,
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user",
                Collections.singleton(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString()))
        );

        User user = new User(
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user"
        );
        user.setId(1L);

        Todo todo = new Todo();
        todo.setUser(user);

        //Mockeamos lo necesario

        Mockito.when(userRepository.getUser(any(UserPrincipal.class))).thenReturn(user);
        Mockito.when(todoRepository.findById(any(Long.class))).thenReturn(Optional.of(todo));

        //Implementamos el test

        Assertions.assertEquals(todo, todoService.getTodo(1L, userPrincipal));

    }

    @Test
    void getTodoResourceNotFoundExceptionTest() {

        //Instanciamos lo necesario

        UserPrincipal userPrincipal = new UserPrincipal(
                1L,
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user",
                Collections.singleton(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString()))
        );

        User user = new User(
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user"
        );
        user.setId(1L);

        Todo todo = new Todo();
        todo.setUser(user);

        //Mockeamos lo necesario

        Mockito.when(userRepository.getUser(any(UserPrincipal.class))).thenReturn(user);
        Mockito.when(todoRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        //Implementamos el test

        Assertions.assertThrows(ResourceNotFoundException.class, () -> todoService.getTodo(1L, userPrincipal));

    }

    @Test
    void getTodoUnauthorizedExceptionTest() {

        //Instanciamos lo necesario

        UserPrincipal userPrincipal = new UserPrincipal(
                1L,
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user",
                Collections.singleton(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString()))
        );

        User user1 = new User(
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user"
        );
        user1.setId(1L);

        User user2 = new User(
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user"
        );
        user2.setId(2L);

        Todo todo = new Todo();
        todo.setUser(user2);

        //Mockeamos lo necesario

        Mockito.when(userRepository.getUser(any(UserPrincipal.class))).thenReturn(user1);
        Mockito.when(todoRepository.findById(any(Long.class))).thenReturn(Optional.of(todo));

        //Implementamos el test

        Assertions.assertThrows(UnauthorizedException.class, () -> todoService.getTodo(1L, userPrincipal));

    }

    @Test
    void updateTodoSuccessTest() {

        //Instanciamos lo necesario

        UserPrincipal userPrincipal = new UserPrincipal(
                1L,
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user",
                Collections.singleton(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString()))
        );

        User user = new User(
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user"
        );
        user.setId(1L);

        Todo todo = new Todo();
        todo.setUser(user);

        Todo newTodo = new Todo();

        //Mockeamos lo necesario

        Mockito.when(userRepository.getUser(any(UserPrincipal.class))).thenReturn(user);
        Mockito.when(todoRepository.findById(any(Long.class))).thenReturn(Optional.of(todo));
        Mockito.when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        //Implementamos el test

        Assertions.assertEquals(todo, todoService.updateTodo(1L, newTodo, userPrincipal));

    }

    @Test
    void updateTodoResourceNotFoundExceptionTest() {

        //Instanciamos lo necesario

        UserPrincipal userPrincipal = new UserPrincipal(
                1L,
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user",
                Collections.singleton(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString()))
        );

        User user = new User(
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user"
        );
        user.setId(1L);

        Todo todo = new Todo();
        todo.setUser(user);

        Todo newTodo = new Todo();

        //Mockeamos lo necesario

        Mockito.when(userRepository.getUser(any(UserPrincipal.class))).thenReturn(user);
        Mockito.when(todoRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        //Implementamos el test

        Assertions.assertThrows(ResourceNotFoundException.class, () -> todoService.updateTodo(1L, newTodo, userPrincipal));

    }

    @Test
    void updateTodoUnauthorizedExceptionTest() {

        //Instanciamos lo necesario

        UserPrincipal userPrincipal = new UserPrincipal(
                1L,
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user",
                Collections.singleton(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString()))
        );

        User user1 = new User(
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user"
        );
        user1.setId(1L);

        User user2 = new User(
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user"
        );
        user2.setId(2L);

        Todo todo = new Todo();
        todo.setUser(user2);

        Todo newTodo = new Todo();

        //Mockeamos lo necesario

        Mockito.when(userRepository.getUser(any(UserPrincipal.class))).thenReturn(user1);
        Mockito.when(todoRepository.findById(any(Long.class))).thenReturn(Optional.of(todo));

        //Implementamos el test

        Assertions.assertThrows(UnauthorizedException.class, () -> todoService.updateTodo(1L, newTodo, userPrincipal));

    }

    @Test
    void deleteTodoSuccessTest() {

        //Instanciamos lo necesario

        UserPrincipal userPrincipal = new UserPrincipal(
                1L,
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user",
                Collections.singleton(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString()))
        );

        User user = new User(
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user"
        );
        user.setId(1L);

        Todo todo = new Todo();
        todo.setUser(user);

        ApiResponse response = new ApiResponse(Boolean.TRUE, "You successfully deleted todo");

        //Mockeamos lo necesario

        Mockito.when(userRepository.getUser(any(UserPrincipal.class))).thenReturn(user);
        Mockito.when(todoRepository.findById(any(Long.class))).thenReturn(Optional.of(todo));
        Mockito.doNothing().when(todoRepository).deleteById(any(Long.class));

        //Implementamos el test

        Assertions.assertEquals(response, todoService.deleteTodo(1L, userPrincipal));

    }

    @Test
    void deleteTodoResourceNotFoundExceptionTest() {

        //Instanciamos lo necesario

        UserPrincipal userPrincipal = new UserPrincipal(
                1L,
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user",
                Collections.singleton(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString()))
        );

        User user = new User(
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user"
        );
        user.setId(1L);

        Todo todo = new Todo();
        todo.setUser(user);

        //Mockeamos lo necesario

        Mockito.when(userRepository.getUser(any(UserPrincipal.class))).thenReturn(user);
        Mockito.when(todoRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        //Implementamos el test

        Assertions.assertThrows(ResourceNotFoundException.class, () -> todoService.deleteTodo(1L, userPrincipal));

    }

    @Test
    void deleteTodoUnauthorizedExceptionTest() {

        //Instanciamos lo necesario

        UserPrincipal userPrincipal = new UserPrincipal(
                1L,
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user",
                Collections.singleton(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString()))
        );

        User user1 = new User(
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user"
        );
        user1.setId(1L);

        User user2 = new User(
                "user",
                "user",
                "user",
                "user@gmail.com",
                "user"
        );
        user2.setId(2L);

        Todo todo = new Todo();
        todo.setUser(user2);

        //Mockeamos lo necesario

        Mockito.when(userRepository.getUser(any(UserPrincipal.class))).thenReturn(user1);
        Mockito.when(todoRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        //Implementamos el test

        Assertions.assertThrows(ResourceNotFoundException.class, () -> todoService.deleteTodo(1L, userPrincipal));

    }

}