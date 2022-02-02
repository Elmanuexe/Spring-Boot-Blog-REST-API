package com.sopromadze.blogapi.service;

import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.payload.AlbumResponse;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.payload.request.AlbumRequest;
import com.sopromadze.blogapi.security.UserPrincipal;
import org.springframework.http.ResponseEntity;

public interface AlbumService {

	PagedResponse<AlbumResponse> getAllAlbums(int page, int size);

	Album addAlbum(AlbumRequest albumRequest, UserPrincipal currentUser);

	Album getAlbum(Long id);

	AlbumResponse updateAlbum(Long id, AlbumRequest newAlbum, UserPrincipal currentUser);

	ApiResponse deleteAlbum(Long id, UserPrincipal currentUser);

	PagedResponse<Album> getUserAlbums(String username, int page, int size);

}
