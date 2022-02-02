package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import static org.junit.jupiter.api.Assertions.*;

import com.sopromadze.blogapi.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Collections;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TestEntityManager testEntityManager;


    @Test
    void findByUserNameSuccess(){
        User user= new User("Luismi","Lopez","Ligre","luismilopezmagaña@salesianos.edu","12345678");
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        testEntityManager.persist(user);
        assertNotNull(userRepository.findByUsername("Ligre").get());
    }

    @Test
    void findByEmail_Success(){
        User user= new User("Luismi","Lopez","Ligre","luismilopezmagaña@salesianos.edu","12345678");
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        testEntityManager.persist(user);
        assertNotNull(userRepository.findByEmail("luismilopezmagaña@salesianos.edu").get());
    }

    @Test
    void existByUserName_Success(){
        User user= new User("Luismi","Lopez","Ligre","luismilopezmagaña@salesianos.edu","12345678");
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        testEntityManager.persist(user);
        assertTrue(userRepository.existsByUsername("Ligre"));
    }

    @Test
    void existByEmail_Success(){
        User user= new User("Luismi","Lopez","Ligre","luismilopezmagaña@salesianos.edu","12345678");
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        testEntityManager.persist(user);
        assertTrue(userRepository.existsByEmail("luismilopezmagaña@salesianos.edu"));
    }

    @Test
    void findByUsernameOrEmail_Success(){
        User user= new User("Luismi","Lopez","Ligre","luismilopezmagaña@salesianos.edu","12345678");
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        testEntityManager.persist(user);
        assertNotNull(userRepository.findByUsernameOrEmail("Ligre","luismilopezmagaña@salesianos.edu"));
    }

    @Test
    void getUserByName_Success(){
        User user= new User("Luismi","Lopez","Ligre","luismilopezmagaña@salesianos.edu","12345678");
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        testEntityManager.persist(user);
        assertNotNull(userRepository.getUserByName("Ligre"));
    }
    @Test
    void getUserByName_ResourceNotFoundException(){
        User user= new User("Luismi","Lopez","Leopardo","luismilopezmagaña@salesianos.edu","12345678");
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        testEntityManager.persist(user);
        assertThrows(ResourceNotFoundException.class,()-> userRepository.getUserByName("Ligre"));
    }

    @Test
    void getUserSuccess (){
        UserPrincipal userPrincipal= new UserPrincipal(1L, "Luismi","Lopez", "Ligre","luismilopezmagaña@salesianos.edu","12345678", Collections.singleton(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString())));
        User user= new User("Luismi","Lopez","Ligre","luismilopezmagaña@salesianos.edu","12345678");
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        testEntityManager.persist(user);
        assertNotNull(userRepository.getUser(userPrincipal));
    }






}