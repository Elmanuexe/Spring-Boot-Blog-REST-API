package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.model.Album;
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


@ActiveProfiles("test")
@DataJpaTest
class AlbumRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    AlbumRepository albumRepository;

    private static Album album;
    private static Pageable pageable;
    private static Long userId;

    //Instanciamos lo necesario
    @BeforeAll
    static void beforeAll() {
        album = new Album();
        album.setTitle("Vacaciones 2020");
        album.setCreatedAt(Instant.now());
        album.setUpdatedAt(Instant.now());
        userId = 1L;
        album.setCreatedBy(userId);

        pageable = PageRequest.of(0, 1);

    }

    @Test
    void findByCreatedByTest() {

        //Comprobación previa de el repositorio
        List<Album> albums = albumRepository.findAll();
        Assumptions.assumeTrue(albums.isEmpty());

        //Guardado de prueba y preparación
        entityManager.persist(album);

        //Implementamos el test

        Assertions.assertAll(
                () -> Assertions.assertFalse(albumRepository.findAll().isEmpty()),
                () -> Assertions.assertFalse(albumRepository.findByCreatedBy(userId, pageable).isEmpty()),
                () -> Assertions.assertEquals("Vacaciones 2020", albumRepository.findByCreatedBy(userId, pageable).get().findFirst().get().getTitle())
        );



    }



}