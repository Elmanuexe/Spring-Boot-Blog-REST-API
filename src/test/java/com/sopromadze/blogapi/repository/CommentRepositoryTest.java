package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.model.Comment;
import com.sopromadze.blogapi.model.Post;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CommentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CommentRepository commentRepository;

    private static Comment comment;
    private static Post post;
    private static Pageable pageable;

    //Inicializamos lo necesario

    @BeforeAll
    static void beforeAll() {

        comment = new Comment("Luismi eres un maquina apruebame porfa");
        comment.setName("Alejandro");
        comment.setEmail("bajo.diale20@triana.salesianos.edu");
        comment.setCreatedAt(Instant.now());
        comment.setUpdatedAt(Instant.now());

        post = new Post();
        post.setCreatedAt(Instant.now());
        post.setUpdatedAt(Instant.now());
        comment.setPost(post);

        pageable = PageRequest.of(0, 1);


    }

    @Test
    void findByPostIdTest() {

        //Persistimos lo necesario y comprobaciones previas

        List<Comment> lista = commentRepository.findAll();
        Assumptions.assumeTrue(lista.isEmpty());
        entityManager.persist(post);
        entityManager.persist(comment);
        lista = commentRepository.findAll();
        Assumptions.assumeFalse(lista.isEmpty());

        Long postId = lista.stream().findFirst().get().getId();

        Page<Comment> comments = commentRepository.findByPostId(postId, pageable);

        //Implementamos el test

        Assertions.assertAll(
                () -> Assertions.assertFalse(comments.isEmpty()),
                () -> Assertions.assertEquals("Alejandro", comments.stream().findFirst().get().getName())
        );

    }

}