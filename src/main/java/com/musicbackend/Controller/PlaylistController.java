package com.musicbackend.Controller;

import com.musicbackend.Dao.PlaylistRepository;
import com.musicbackend.Dao.SongRepository;
import com.musicbackend.Dao.UserRepository;
import com.musicbackend.Entity.PlaylistSongRequest;
import com.musicbackend.Entity.Playlist;
import com.musicbackend.Entity.Song;
import com.musicbackend.Entity.User;
import com.musicbackend.Helper.PayLoad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.SpringCacheAnnotationParser;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/playlist")
public class PlaylistController {

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/myPlaylist")
    public ResponseEntity<?> myPlaylist(Principal principal) {
        try {
            User user = this.userRepository.findByEmail(principal.getName());
            List<Playlist> playlists = this.playlistRepository.findAllByUser(user);

            if (playlists.isEmpty()) {
                return ResponseEntity.ok(
                        new PayLoad("You Have No Playlist", "Success", LocalDateTime.now(), null)
                );
            }

            return ResponseEntity.ok(
                    new PayLoad("Your Playlists", "Success", LocalDateTime.now(), playlists)
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new PayLoad("Error While Fetching Users Playlist", "Failure", LocalDateTime.now(), null)
            );
        }
    }

    @GetMapping("/getPlaylist/{pId}")
    public ResponseEntity<?> getPlaylistSong(@PathVariable Integer pId, Principal principal) {
        try {
            Playlist playlist = this.playlistRepository.findById(pId).orElse(null);
            if (playlist == null) {
                return ResponseEntity.ok(
                        new PayLoad("No Such Playlist Exist", "Success", LocalDateTime.now(), null)
                );
            }

            return ResponseEntity.ok(
                    new PayLoad("Playlist", "Success", LocalDateTime.now(), playlist)
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new PayLoad("Error Occurred While Fetching The Playlist", "Failure",
                            LocalDateTime.now(), null)
            );
        }
    }

    @PostMapping("/addSongToPlaylist")
    public ResponseEntity<?> addSongToPlaylist(@RequestBody PlaylistSongRequest addSongToPlaylist) {
        try {
            int playlistId = addSongToPlaylist.getPlaylistId();
            int songId = addSongToPlaylist.getSongId();
            Playlist playlist = this.playlistRepository.findById(playlistId).orElse(null);
            Song song = this.songRepository.findById(songId).orElse(null);

            if (playlist == null) {
                return ResponseEntity.ok(
                        new PayLoad("No Such Playlist", "Success", LocalDateTime.now(), null)
                );
            }

            if (song == null) {
                return ResponseEntity.ok(
                        new PayLoad("No Such Song", "Success", LocalDateTime.now(), null)
                );
            }

            if (!playlist.getSongs().contains(song))
                playlist.getSongs().add(song);

            playlist = this.playlistRepository.save(playlist);
            return ResponseEntity.ok(
                    new PayLoad("Added The Song to Playlist", "Success", LocalDateTime.now(), playlist)
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new PayLoad("Error While Adding Songs to Playlist", "Failure", LocalDateTime.now(), null)
            );
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteSong(@RequestBody PlaylistSongRequest request) {
        try {
            int playlistId = request.getPlaylistId();
            int songId = request.getSongId();

            Playlist playlist = this.playlistRepository.findById(playlistId).orElse(null);
            Song song = this.songRepository.findById(songId).orElse(null);

            if (playlist == null) {
                return ResponseEntity.ok(
                        new PayLoad("No Such Playlist", "Success", LocalDateTime.now(), null)
                );
            }

            if (song == null) {
                return ResponseEntity.ok(
                        new PayLoad("No Such Song", "Success", LocalDateTime.now(), null)
                );
            }

            if (!playlist.getSongs().contains(song)) {
                return ResponseEntity.ok(
                        new PayLoad("The Playlist does not have this Song", "Success", LocalDateTime.now(), null)
                );
            }

            playlist.getSongs().remove(song);
            playlist = this.playlistRepository.save(playlist);

            return ResponseEntity.ok(
                    new PayLoad("Deleted The Song From Playlist", "Success", LocalDateTime.now(), playlist)
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new PayLoad("Error While Deleting The Song", "Failure", LocalDateTime.now(), null)
            );
        }
    }

    @DeleteMapping("/delete/{playlistId}")
    public ResponseEntity<?> deletePlaylist(@PathVariable Integer playlistId, Principal principal) {
        try {
            User user = this.userRepository.findByEmail(principal.getName());
            Playlist playlist = this.playlistRepository.findById(playlistId).orElse(null);

            if (playlist == null) {
                return ResponseEntity.ok(
                        new PayLoad("No Such Playlist", "Success", LocalDateTime.now(), null)
                );
            }

            if (user != playlist.getCreatedBy()) {
                return ResponseEntity.ok(
                        new PayLoad("This is Not Your Playlist", "Success", LocalDateTime.now(), null)
                );
            }

            this.playlistRepository.delete(playlist);

            return ResponseEntity.ok(
                    new PayLoad("Deleted The Playlist Successfully", "Success", LocalDateTime.now(), null)
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new PayLoad("Error While Deleting The Playlist", "Failure", LocalDateTime.now(), null)
            );
        }
    }

    @PostMapping("/update/{playlistId}")
    public ResponseEntity<?> renamePlaylist(@RequestBody Playlist playlist, @PathVariable Integer playlistId
            , Principal principal) {
        try {
            Playlist playlistDb = this.playlistRepository.findById(playlistId).orElse(null);
            User user = this.userRepository.findByEmail(principal.getName());
            if (playlistDb == null) {
                return ResponseEntity.ok(
                        new PayLoad("No Such Playlist Exist", "Success", LocalDateTime.now(), null)
                );
            }

            if (!playlistDb.getCreatedBy().equals(user)) {
                return ResponseEntity.ok(
                        new PayLoad("You are Not Authorized to Update this Playlist", "Success", LocalDateTime.now(), null)
                );
            }

            playlistDb.setTitle(playlist.getTitle());
            playlistDb.setDescription(playlist.getDescription());
            playlist = this.playlistRepository.save(playlistDb);
            return ResponseEntity.ok(
                    new PayLoad("Updated The Playlist", "Success", LocalDateTime.now(), playlist)
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new PayLoad("Error WHile Renaming The Playlist", "Failure", LocalDateTime.now(), null)
            );
        }
    }
}
