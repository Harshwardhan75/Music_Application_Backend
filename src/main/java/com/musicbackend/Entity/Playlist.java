package com.musicbackend.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "playlist")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int playlistId;

    private String title;
    private String description;
    @ManyToOne
    @JoinColumn(name = "userId")
    private User createdBy;

    @ManyToMany
    @JoinTable(
            name = "playlist_song",
            joinColumns = @JoinColumn(name = "playlistId"),
            inverseJoinColumns = @JoinColumn(name = "songId")
    )
    private Set<Song> songs;
}
