package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.model.Category;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.Tag;
import com.sopromadze.blogapi.model.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PostRepositoryTest {

    @Autowired
    PostRepository postRepository;

    @Autowired
    TestEntityManager testEntityManager;

    @Test
    void findByCreatedBy() {
        User userP = new User("Manuel", "Fernández", "ManuFer", "manufer@gmail.com", "123456789");
        userP.setCreatedAt(Instant.now());
        userP.setUpdatedAt(Instant.now());
        testEntityManager.persist(userP);

        List<Post> postse = new ArrayList<>();

        Post post1 = new Post();
        post1.setTitle("titulo");
        post1.setBody("body");
        post1.setUser(userP);
        post1.setCreatedBy(userP.getId());
        post1.setCreatedAt(Instant.now());
        post1.setUpdatedAt(Instant.now());
        testEntityManager.persist(post1);

        postse.add(post1);

        Pageable pageable = PageRequest.of(0, 1, Sort.Direction.DESC, "createdAt");

        Page<Post> posts = postRepository.findByCreatedBy(userP.getId(), pageable);
        assertThat(posts).hasSize(1).contains(post1);
    }

    @Test
    void findByCategory() {
        Category cat = new Category();

        Post post1 = new Post();
        post1.setCategory(cat);
        post1.setCreatedAt(Instant.now());
        post1.setUpdatedAt(Instant.now());
        testEntityManager.persist(post1);

        //cat.setPosts(List.of(post1));
        cat.setCreatedAt(Instant.now());
        cat.setUpdatedAt(Instant.now());
        testEntityManager.persist(cat);

        Pageable pageable = PageRequest.of(0, 1, Sort.Direction.DESC, "createdAt");

        Page<Post> posts = postRepository.findByCategoryId(cat.getId(), pageable);

        assertThat(posts).contains(post1);

    }

    @Test
    void findByTagsIn() {

        Tag t = new Tag();
        t.setCreatedAt(Instant.now());
        t.setUpdatedAt(Instant.now());
        testEntityManager.persist(t);

        Post p = new Post();
        p.setCreatedAt(Instant.now());
        p.setUpdatedAt(Instant.now());
        p.setTags(List.of(t));
        testEntityManager.persist(p);

        Pageable pageable = PageRequest.of(0, 1, Sort.Direction.DESC, "createdAt");

        Iterable<Post> posts = postRepository.findByTagsIn(List.of(t), pageable);

        assertThat(posts).hasSize(1).contains(p);

    }

    @Test
    void countByCreatedBy() {

        User userP = new User("Manuel", "Fernández", "ManuFer", "manufer@gmail.com", "123456789");
        userP.setCreatedAt(Instant.now());
        userP.setUpdatedAt(Instant.now());

        Post p = new Post();
        p.setCreatedAt(Instant.now());
        p.setUpdatedAt(Instant.now());
        p.setCreatedBy(userP.getId());
        testEntityManager.persist(p);

        Long cuenta =  postRepository.countByCreatedBy(userP.getId());

        assertEquals(1, cuenta);

    }
}