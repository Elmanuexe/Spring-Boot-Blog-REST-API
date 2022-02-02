package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.Photo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PhotoRepositoryTest {

    @Autowired
    PhotoRepository photoRepository;

    @Autowired
    TestEntityManager testEntityManager;


    private static List<Photo> photos = new ArrayList<>();
    private static Album al;

    @BeforeEach
    void setUp() {

        Photo photo1 = new Photo();
        photo1.setUrl("www.photo1.com");
        photo1.setThumbnailUrl("www.photo1.com");
        photo1.setTitle("Photo1");
        photo1.setCreatedAt(Instant.now());
        photo1.setUpdatedAt(Instant.now());

        Photo photo2 = new Photo();
        photo2.setUrl("www.photo2.com");
        photo2.setThumbnailUrl("www.photo2.com");
        photo2.setTitle("Photo2");
        photo2.setCreatedAt(Instant.now());
        photo2.setUpdatedAt(Instant.now());

        photos.add(photo1);
        photos.add(photo2);

        al = new Album();
        al.setPhoto(photos);
        al.setTitle("Album Fotos");
        al.setUpdatedAt(Instant.now());
        al.setCreatedAt(Instant.now());
    }
    @Test
    void findByAlbumId_Success() {
        testEntityManager.persist(al);
        assertNotNull(photoRepository.findByAlbumId(al.getId(), PageRequest.of(1, 1, Sort.Direction.DESC,"createdAt")));
    }
}