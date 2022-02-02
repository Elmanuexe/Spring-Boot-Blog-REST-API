package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.Tag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class TagRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    TagRepository tagRepository;

    private static Tag tag;
    private static Pageable pageable;
    private static Long tagId;

    //Instanciamos lo necesario
    @BeforeAll
    static void beforeAll() {
        tag = new Tag("Fiestas");
        tag.setCreatedAt(Instant.now());
        tag.setUpdatedAt(Instant.now());

    }

    @Test
    void findByCreatedByTest() {

        //Comprobación previa de el repositorio
        List<Tag> tags = tagRepository.findAll();
        Assumptions.assumeTrue(tags.isEmpty());

        //Guardado de prueba y preparación

        entityManager.persist(tag);
        tagId = tag.getId();

        //Implementamos el test

        Assertions.assertAll(
                () -> Assertions.assertFalse(tagRepository.findAll().isEmpty()),
                () -> Assertions.assertEquals(tagId, tagRepository.findByName("Fiestas").getId())
        );
    }
}