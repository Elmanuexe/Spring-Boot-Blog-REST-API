package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RoleRepositoryTest {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    TestEntityManager testEntityManager;

    private static Role role;

    @BeforeEach
    void setUp() {

        role = new Role();
        role.setName(RoleName.ROLE_ADMIN);
    }

    @Test
    void findByName_Success() {
        testEntityManager.persist(role);
        assertNotNull(roleRepository.findByName(role.getName()));
        assertEquals(Optional.of(role),roleRepository.findByName(RoleName.ROLE_ADMIN));
    }
}