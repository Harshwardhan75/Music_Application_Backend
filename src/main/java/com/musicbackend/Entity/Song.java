package com.musicbackend.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "song")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int songId;

    private String title;
    private String artist;
    private String path;
    private LocalDate uploadDate;

    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonIgnore
    private User uploadedBy;

    @ManyToMany
    @JoinTable(
            name = "song_likes",
            joinColumns = @JoinColumn(name = "songId"),
            inverseJoinColumns = @JoinColumn(name = "userId")
    )
    private Set<User> likedBy;
}
