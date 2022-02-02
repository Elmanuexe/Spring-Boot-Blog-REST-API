package com.sopromadze.blogapi.repository;
import org.junit.jupiter.api.BeforeEach;
import com.sopromadze.blogapi.model.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TodoRepositoryTest {

    @Autowired
    TodoRepository todoRepository;

    @Autowired
    TestEntityManager testEntityManager;

    private static User user;

    @BeforeEach
    void setUp() {

        user = new User("Pepe","Palomo","pepepalomo","pepepalomo@gmail.com","658");
        user.setCreatedAt(Instant.now());
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
    }

    @Test
    void findByCreatedBy_Success() {
        testEntityManager.persist(user);
        assertNotNull(todoRepository.findByCreatedBy(user.getId(), PageRequest.of(1, 1, Sort.Direction.DESC,"createdAt")));
    }
}