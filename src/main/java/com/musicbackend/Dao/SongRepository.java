package com.musicbackend.Dao;

import com.musicbackend.Entity.Song;
import com.musicbackend.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SongRepository extends JpaRepository<Song, Integer> {

    @Query("from Song s where lower(s.title) like lower(concat('%',:title,'%'))")
    List<Song> findByTitle(String title);

    @Query("from Song s where lower(s.artist) like lower(concat('%',:artist,'%'))")
    List<Song> findByArtist(String artist);

    @Query("from Song s where :user member of s.likedBy")
    List<Song> findLikedByUser(User user);
}
