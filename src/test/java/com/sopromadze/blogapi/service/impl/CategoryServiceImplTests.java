package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.exception.UnauthorizedException;
import com.sopromadze.blogapi.model.Category;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.repository.CategoryRepository;
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
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.*;

import static com.sopromadze.blogapi.model.role.RoleName.ROLE_ADMIN;
import static com.sopromadze.blogapi.model.role.RoleName.ROLE_USER;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CategoryServiceImplTests {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryServiceImpl;


    @Test
    void getAllCategoriesSuccessTest() {

        //Instanciamos lo necesario

        Category category1 = new Category(
                "paisaje"
        );
        category1.setCreatedAt(Instant.now());

        Category category2 = new Category(
                "Montañas"
        );
        category2.setCreatedAt(Instant.now());

        Category category3 = new Category(
                "Playas"
        );
        category3.setCreatedAt(Instant.now());

        Pageable pageable = PageRequest.of(0, 3, Sort.Direction.DESC, "createdAt");

        List<Category> list = List.of(category1, category2, category3);
        Page<Category> page = new PageImpl<>(list);

        PagedResponse<Category> pagedResponse = new PagedResponse<>(list, 0, 3, 3L, 1, true);

        //Mockeamos lo necesario

        Mockito.when(categoryRepository.findAll(pageable)).thenReturn(page);

        //Implementamos el test

        Assertions.assertEquals(pagedResponse, categoryServiceImpl.getAllCategories(0, 3));

    }

    @Test
    void getCategorySuccessTest() {

        //Instanciamos lo necesario.

        Category category = new Category(
                "paisaje"
        );
        category.setId(1L);
        Optional<Category> c = Optional.of(category);

        Mockito.when(categoryRepository.findById(1l)).thenReturn(c);

        //Implementamos el test

        Assertions.assertEquals(category, categoryServiceImpl.getCategory(1L));

    }

    @Test
    void getCategoryExceptionTest() {

        //Instanciamos lo necesario

        Optional<Category> category = Optional.empty();

        Mockito.when(categoryRepository.findById(1L)).thenReturn(category);

        //Implementamos el test

        Assertions.assertThrows(ResourceNotFoundException.class, () -> categoryServiceImpl.getCategory(1L));

    }

    @Test
    void addCategorySuccessTest() {

        //Instanciamos lo necesario

        Category category = new Category(
                "paisaje"
        );
        category.setId(1L);

        UserPrincipal currentUser = new UserPrincipal(
                1L,
                "Ale",
                "Bajo", "diale",
                "luismimaquina@gmail.com",
                "1234",
                null
        );

        //Mockeamos lo necesario

        Mockito.when(categoryServiceImpl.addCategory(category, currentUser)).thenReturn(category);

        //Implementamos el test

        Assertions.assertEquals(category, categoryServiceImpl.addCategory(category, currentUser));

    }

    @Test
    void updateCategorySuccessTest() {

        //Instanciamos lo necesario

        Category category = new Category("Paisajes");
        category.setId(1L);
        category.setCreatedBy(1L);

        Category newCategory = new Category("Montañas");
        newCategory.setId(1L);

        User user = new User(
                "Ale",
                "Bajo",
                "diale",
                "luismimaquina@gmail.com",
                "12345678"
        );
        user.setId(1L);
        Role role = new Role(ROLE_ADMIN);
        List<Role> roles = List.of(role);
        user.setRoles(roles);

        UserPrincipal currentUser = UserPrincipal.create(user);

        //Mockeamos lo necesario

        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        Mockito.when(categoryRepository.save(newCategory)).thenReturn(newCategory);

        //Implantamos el test

        Assertions.assertEquals(newCategory.getId(), categoryServiceImpl.updateCategory(1L, newCategory, currentUser).getId());

    }

    @Test
    void updateCategoryResourceNotFoundExceptionTest() {

        //Instanciamos lo necesario

        Category category = new Category("Paisajes");
        category.setId(1L);

        UserPrincipal currentUser = new UserPrincipal(
                1L,
                "Ale",
                "Bajo", "diale",
                "luismimaquina@gmail.com",
                "1234",
                null
        );

        //Mockeamos lo necesario

        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        //Implantamos el test

        Assertions.assertThrows(ResourceNotFoundException.class, () -> categoryServiceImpl.updateCategory(1L, category, currentUser));

    }

    @Test
    void updateCategoryUnauthorizedExceptionTest() {

        //Instanciamos lo necesario

        Category category = new Category("Paisajes");
        category.setId(1L);
        category.setCreatedBy(1L);

        Category newCategory = new Category("Montañas");
        newCategory.setId(1L);

        User badUser = new User(
                "Ale",
                "Bajo",
                "diale",
                "luismimaquina@gmail.com",
                "12345678"
        );
        badUser.setId(5L);
        Role role1 = new Role(ROLE_USER);
        List<Role> roles1 = List.of(role1);
        badUser.setRoles(roles1);

        UserPrincipal badUserPr = UserPrincipal.create(badUser);

        //Mockeamos lo necesario

        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        //Implantamos el test

        Assertions.assertThrows(UnauthorizedException.class, () -> categoryServiceImpl.updateCategory(1L, newCategory, badUserPr));

    }

    @Test
    void deleteCategorySuccessTest() {

        //Instanciamos lo necesario

        Category category = new Category("Paisajes");
        category.setId(1L);
        category.setCreatedBy(1L);

        User user = new User(
                "Ale",
                "Bajo",
                "diale",
                "luismimaquina@gmail.com",
                "12345678"
        );
        user.setId(1L);
        Role role = new Role(ROLE_ADMIN);
        List<Role> roles = List.of(role);
        user.setRoles(roles);

        UserPrincipal currentUser = UserPrincipal.create(user);

        ResponseEntity<ApiResponse> responseEntity = new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "You successfully deleted category"), HttpStatus.OK);

        //Mockeamos lo necesario

        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        Mockito.doNothing().when(categoryRepository).deleteById(1L);

        //Implementamos el test

        Assertions.assertEquals(responseEntity, categoryServiceImpl.deleteCategory(1L, currentUser));

    }

    @Test
    void deleteCategoryResourceNotFoundExceptionTest() {

        //Instanciamos lo necesario

        UserPrincipal currentUser = new UserPrincipal(
                1L,
                "Ale",
                "Bajo", "diale",
                "luismimaquina@gmail.com",
                "1234",
                null
        );

        //Mockeamos lo necesario

        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        //Implantamos el test

        Assertions.assertThrows(ResourceNotFoundException.class, () -> categoryServiceImpl.deleteCategory(1L, currentUser));

    }

    @Test
    void deleteCategoryUnauthorizedExceptionTest() {

        //Instanciamos lo necesario

        Category category = new Category("Paisajes");
        category.setId(1L);
        category.setCreatedBy(1L);

        User badUser = new User(
                "Ale",
                "Bajo",
                "diale",
                "luismimaquina@gmail.com",
                "12345678"
        );
        badUser.setId(5L);
        Role role1 = new Role(ROLE_USER);
        List<Role> roles1 = List.of(role1);
        badUser.setRoles(roles1);

        UserPrincipal badUserPr = UserPrincipal.create(badUser);

        //Mockeamos lo necesario

        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        //Implantamos el test

        Assertions.assertThrows(UnauthorizedException.class, () -> categoryServiceImpl.deleteCategory(1L, badUserPr));

    }
}
