package com.sopromadze.blogapi.service;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.exception.UnauthorizedException;
import com.sopromadze.blogapi.model.Tag;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.repository.TagRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.impl.TagServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    TagServiceImpl tagService;

    static Tag t;

    @BeforeEach
    void init(){
        t = new Tag("yoquese");
        t.setCreatedBy(6L);
        t.setId(4L);
    }

    @Test
    void getTagById_success(){

        when(tagRepository.findById(anyLong())).thenReturn(Optional.of(t));

        assertEquals(t, tagService.getTag(4L));
    }

    @Test
    void getTagById_failure(){

        when(tagRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tagService.getTag(4L));
    }

    @Test
    void addTag_success(){

        Role r = new Role(RoleName.ROLE_USER);

        List<Role> roles = new ArrayList<Role>();
        roles.add(r);

        User user = new User("Manuel", "Fernández", "ManuFer", "manufer@gmail.com", "123456789");
        user.setRoles(roles);

        UserPrincipal userP = UserPrincipal.create(user);

        when(tagRepository.save(t)).thenReturn(t);

        assertEquals(t, tagService.addTag(t, userP));

    }

    @Test
    void addTag_fail(){

        Tag t = new Tag(null);

        Role r = new Role(RoleName.ROLE_USER);

        List<Role> roles = new ArrayList<Role>();
        roles.add(r);

        User user = new User("Manuel", "Fernández", "ManuFer", "manufer@gmail.com", "123456789");
        user.setRoles(roles);

        UserPrincipal userP = UserPrincipal.create(user);

        when(tagRepository.save(t)).thenReturn(null);

        assertEquals(null, tagService.addTag(t, userP));

    }

    @Test
    void deleteTag_success(){

        UserPrincipal userP = new UserPrincipal(1L,"Manuel", "Fernández", "ManuFer", "manufer@gmail.com", "123456789", Collections.singleton(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString())));

        when(tagRepository.findById(anyLong())).thenReturn(Optional.of(t));

        ApiResponse apiResponse = tagService.deleteTag(5L, userP);

        verify(tagRepository).deleteById(5L);

        assertTrue(apiResponse.getSuccess());
    }

    @Test
    void deleteTag_failure(){

        UserPrincipal userP = new UserPrincipal(1L,"Manuel", "Fernández", "ManuFer", "manufer@gmail.com", "123456789", Collections.singleton(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString())));

        when(tagRepository.findById(anyLong())).thenReturn(Optional.of(t));

        assertThrows(UnauthorizedException.class, () -> tagService.deleteTag(4L, userP));
    }

    @Test
    void updateTag_success(){

        UserPrincipal userP = new UserPrincipal(1L,"Manuel", "Fernández", "ManuFer", "manufer@gmail.com", "123456789", Collections.singleton(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString())));

        when(tagRepository.findById(anyLong())).thenReturn(Optional.of(t));

        Tag nt;
        nt = new Tag("hola");
        nt.setCreatedBy(1L);
        nt.setId(4L);

        when(tagRepository.save(t)).thenReturn(t);

        assertEquals(t, tagService.updateTag(4L, nt, userP));
    }

    @Test
    void pagedAllTags_success(){

        Pageable pageable = PageRequest.of(1, 1, Sort.Direction.DESC, "createdAt");

        List<Tag> tags = new ArrayList<>();
        tags.add(t);

        Page tagPage = new PageImpl<Tag>(tags);

        when(tagRepository.findAll(pageable)).thenReturn(tagPage);

        assertEquals(1, tagService.getAllTags(1, 1).getSize());

    }

}