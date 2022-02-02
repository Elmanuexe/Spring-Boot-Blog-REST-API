package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.*;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.Address;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.*;
import com.sopromadze.blogapi.repository.PostRepository;
import com.sopromadze.blogapi.repository.RoleRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    UserServiceImpl userService;

    private static UserPrincipal userPrincipal;
    private static UserPrincipal userPrincipalUser;
    private static User user;
    private static Role role;
    private static Role role2;
    private static InfoRequest infoRequest;

    @BeforeEach
    void setUp() {
        userPrincipal = new UserPrincipal(12L, "Pepe", "Palomo",
                "pepepalomo", "pepepalomo@gmail.com", "1234",
                Collections.singleton(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString())));

        userPrincipalUser = new UserPrincipal(56L, "Paola", "Carmona",
                "paocarm", "paocarm@gmail.com", "1234",
                Collections.singleton(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString())));

        Address address = new Address();
        address.setId(2L);
        address.setCity("Sevilla");
        address.setStreet("Calle x");

        user = new User();
        user.setUsername("pepepalomo");
        user.setId(12L);
        user.setCreatedAt(Instant.now());
        user.setFirstName("Pepe");
        user.setLastName("Palomo");
        user.setAddress(address);
        user.setPhone("654548798");
        user.setWebsite("www.palomo.com");

        role = new Role();
        role.setName(RoleName.ROLE_USER);

        role2 = new Role();
        role2.setName(RoleName.ROLE_ADMIN);

        infoRequest = new InfoRequest();
        infoRequest.setCity("Sevilla");
        infoRequest.setCompanyName("Empresa");
        infoRequest.setZipcode("554");
    }

    @Test
    //Dato de entrada Id usuario, dato de salida Usuario Logeado
    void getCurrentUser_Success() {
        UserSummary userSummary = userService.getCurrentUser(userPrincipal);
        assertEquals(userPrincipal.getId(), userSummary.getId(), "Este método te devuelve el usuario logeado");
    }

    @Test
    //Dato de entrada Nombre de Usuario sin registrar,  dato de salida un true ya que no se encuentra el nombre registrado
    void checkUsernameAvailability_Success() {
        when(userRepository.existsByUsername(userPrincipal.getUsername())).thenReturn(false);

        UserIdentityAvailability userIdentityAvailability = userService.checkUsernameAvailability(userPrincipal.getUsername());
        assertTrue(userIdentityAvailability.getAvailable().booleanValue(),
                "Este método comprueba si el nombre de usuario es válido");
    }

    @Test
    //Dato de entrada un Email, dato de salida un true porque no se encuentra registrado
    void checkEmailAvailability_Success() {
        when(userRepository.existsByEmail(userPrincipal.getEmail())).thenReturn(false);

        UserIdentityAvailability userIdentityAvailability = userService.checkUsernameAvailability(userPrincipal.getEmail());
        assertTrue(userIdentityAvailability.getAvailable().booleanValue(),
                "Este método comprueba si el email de usuario es válido");
    }

    @Test
    //Dato de entrada el Id, dato de salida el perfil de usuario del Id
    void getUserProfile_Success() {
        when(userRepository.getUserByName(user.getUsername())).thenReturn(user);
        when(postRepository.countByCreatedBy(user.getId())).thenReturn(user.getId());

        UserProfile userProfile = userService.getUserProfile(user.getUsername());
        assertEquals(user.getId(), userProfile.getId());
    }

    @Test
    //Dato de entrada un usuario, dato de salida el usuario registrado
    void addUser_Success() {
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.of(role));
        when(userRepository.save(user)).thenReturn(user);

        User u = userService.addUser(user);
        assertEquals(user.getId(),u.getId());
    }

    @Test
    //Dato de entrada un nombre registrado, dato de salida BadRequest
    void addUser_BadRequest(){
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);

        assertThrows(BadRequestException.class, ()-> userService.addUser(user),
                "Username is already taken");
    }

    @Test
    //Dato de entrada email ya registrado, dato de salida BadRequest
    void addUser_BadRequestEmail(){
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        assertThrows(BadRequestException.class, ()-> userService.addUser(user),
                "Email is already taken");
    }

    @Test
    //Dato de entrada usuario sin rol, dato de salida AppException
    void addUser_AppException(){
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.empty());

        assertThrows(AppException.class, ()-> userService.addUser(user),
                "User role not set");
    }

    @Test
    //Dato de entrada usuario, dato de salida usuario actualizado
    void updateUser_Success() {
        when(userRepository.getUserByName(user.getUsername())).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        User usuarioUpdate = userService.updateUser(user, user.getUsername(), userPrincipal);
        assertEquals(userPrincipal.getId(), usuarioUpdate.getId());
    }

    @Test
    //Dato de entrada usuario sin role de admin, dato de salida UnauthorizedException
    void updateUser_Unauthorized(){
        when(userRepository.getUserByName(user.getUsername())).thenReturn(user);

        assertThrows(UnauthorizedException.class, ()-> userService.updateUser(user, user.getUsername(), userPrincipalUser),
                "\"You don't have permission to update profile of: \" + username");
    }

    @Test
    //Dato de entrada usuario, dato de salida ApiResponse (True)
    void deleteUser_Success() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        ApiResponse apiResponse = userService.deleteUser(user.getUsername(), userPrincipal);
        verify(userRepository).deleteById(user.getId());
        assertTrue(apiResponse.getSuccess());
    }

    @Test
    //Dato de entrada nulo, dato de salida ResourceNotFoundException
    void deleteUser_NotFound(){
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, ()-> userService.deleteUser(user.getUsername(), userPrincipal),
                "\"User\", \"id\", username");
    }

    @Test
    //Dato de entrada user sin role de ADMIN, dato de salida AccessDeniedException
    void deleteUser_AccesDenied(){
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        user.setId(548L);

        assertThrows(AccessDeniedException.class, ()-> userService.deleteUser(user.getUsername(), userPrincipal),
                "\"You don't have permission to delete profile of: \" + username");
    }

    @Test
    //Dato de entrada usuario, dato de salida usuario con role de ADMIN
    void giveAdmin_Success() {
        when(userRepository.getUserByName(user.getUsername())).thenReturn(user);
        when(roleRepository.findByName(role.getName())).thenReturn(Optional.of(role));
        when(roleRepository.findByName(role2.getName())).thenReturn(Optional.of(role2));
        when(userRepository.save(user)).thenReturn(user);

        ApiResponse apiResponse = userService.giveAdmin(user.getUsername());
        assertTrue(apiResponse.getSuccess());
        //assertTrue(user.getRoles().contains(RoleName.ROLE_ADMIN));
    }

    @Test
    //Dato de entrada Usuario sin role de Admin, dato de salida AppException
    void giveAdmin_AppException(){
        when(userRepository.getUserByName(user.getUsername())).thenReturn(user);
        when(roleRepository.findByName(role.getName())).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> userService.giveAdmin(user.getUsername()),
                "User role not set");
    }

    @Test
    //Dato de entrada Usuario con role de Admin pero sin role de usuario, dato de salida AppException
    void giveAdmin_AppExceptionRole(){
        when(userRepository.getUserByName(user.getUsername())).thenReturn(user);
        when(roleRepository.findByName(role.getName())).thenReturn(Optional.of(role2));
        when(roleRepository.findByName(role.getName())).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> userService.giveAdmin(user.getUsername()),
                "\"You gave ADMIN role to user: \" + username");
    }

    @Test
    //Dato de entrada usuario con role de admin, dato de salida usuario sin role de admin
    void removeAdmin_Success() {
        user.setRoles(List.of(role2));
        when(userRepository.getUserByName(user.getUsername())).thenReturn(user);
        when(roleRepository.findByName(role.getName())).thenReturn(Optional.of(role));
        when(userRepository.save(user)).thenReturn(user);

        ApiResponse apiResponse = userService.removeAdmin(user.getUsername());
        assertTrue(apiResponse.getSuccess());
        assertFalse(user.getRoles().contains(RoleName.ROLE_ADMIN));
    }

    @Test
    //Dato de entrada usuario sin role, dato de salida AppException
    void removeAdmin_AppException(){
        when(userRepository.getUserByName(user.getUsername())).thenReturn(user);
        when(roleRepository.findByName(role.getName())).thenReturn(Optional.empty());

        assertThrows(AppException.class, ()-> userService.removeAdmin(user.getUsername()),
                "\"You took ADMIN role from user: \" + username");
    }

    @Test
    //Dato de entrada datos de usuario, datos de salida datos de usuario actualizados
    void setOrUpdateInfo_Success() {
        when(userRepository.findByUsername(userPrincipal.getUsername())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(postRepository.countByCreatedBy(user.getId())).thenReturn(1L);

        UserProfile userProfile = userService.setOrUpdateInfo(userPrincipal, infoRequest);
        assertEquals(user.getId(), userProfile.getId());
    }

    @Test
    //Dato de entrada usuario no existente, dato de salida ResourceNotFoundException
    void setOrUpdateInfo_NotFound(){
        when(userRepository.findByUsername(userPrincipal.getUsername())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, ()-> userService.setOrUpdateInfo(userPrincipal, infoRequest),
                "\"User\", \"username\", currentUser.getUsername()");
    }

    @Test
    //Dato de entrada usuario sin role de admin, dato de salida AccessDeniedException
    void setOrUpdateInfo_AccessDenied(){
        when(userRepository.findByUsername(userPrincipalUser.getUsername())).thenReturn(Optional.of(user));

        assertThrows(AccessDeniedException.class, ()-> userService.setOrUpdateInfo(userPrincipalUser, infoRequest),
                "You don't have permission to update users profile");
    }
}