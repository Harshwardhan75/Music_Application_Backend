package com.musicbackend.Dao;

import com.musicbackend.Entity.Playlist;
import com.musicbackend.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, Integer> {

    @Query("from Playlist p where p.createdBy = :user")
    List<Playlist> findAllByUser(User user);
}
