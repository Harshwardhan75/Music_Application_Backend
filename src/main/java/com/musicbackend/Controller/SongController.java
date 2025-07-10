package com.musicbackend.Controller;

import com.musicbackend.Dao.SongRepository;
import com.musicbackend.Dao.UserRepository;
import com.musicbackend.Entity.Song;
import com.musicbackend.Entity.User;
import com.musicbackend.Helper.PayLoad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.cglib.core.internal.LoadingCache;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/song")
public class SongController {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/like/{songId}")
    public ResponseEntity<?> likeSong(@PathVariable Integer songId, Principal principal) {
        try {
            Song song = this.songRepository.findById(songId).orElse(null);
            if (song == null) {
                return ResponseEntity.ok(
                        new PayLoad("No Such Song", "Success", LocalDateTime.now(), null)
                );
            }

            User user = this.userRepository.findByEmail(principal.getName());

            if (song.getLikedBy().contains(user)) {
                song.getLikedBy().remove(user);
                this.songRepository.save(song);
                return ResponseEntity.ok(
                        new PayLoad("Unliked the Song", "Success", LocalDateTime.now(), song)
                );
            }

            song.getLikedBy().add(user);
            this.songRepository.save(song);
            return ResponseEntity.ok(
                    new PayLoad("Liked the Song", "Success", LocalDateTime.now(), song)
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new PayLoad("Error Occured while Liking The Song", "Failure", LocalDateTime.now(), null)
            );
        }
    }

    @GetMapping("/songList/{pageNo}")
    public ResponseEntity<?> songList(@PathVariable Integer pageNo) {
        try {
            PageRequest pageRequest = PageRequest.of(pageNo, 5);
            Page<Song> songs = this.songRepository.findAll(pageRequest);
            if (songs.isEmpty()) {
                return ResponseEntity.ok(
                        new PayLoad("No songs Available", "Success", LocalDateTime.now(), songs)
                );
            }
            return ResponseEntity.ok(
                    new PayLoad("All songs Page:" + songs.getNumber(), "Success", LocalDateTime.now(), songs.getContent())
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new PayLoad("Error While Fetching Songs", "Failure", LocalDateTime.now(), null)
            );
        }
    }

    @GetMapping("/download/{songId}")
    public ResponseEntity<?> downloadSong(@PathVariable Integer songId) {
        try {
            Song song = this.songRepository.findById(songId).orElse(null);
            if (song == null) {
                return ResponseEntity.ok(
                        new PayLoad("No Such Song", "Success", LocalDateTime.now(), null)
                );
            }

            File file = new File(song.getPath());

            if (!file.exists()) {
                return ResponseEntity.internalServerError().body(
                        new PayLoad("Song Not Found", "Success", LocalDateTime.now(), null)
                );
            }

            InputStream inputStream = new FileInputStream(file);
            byte[] out = inputStream.readAllBytes();
            inputStream.close();

            return ResponseEntity.ok()
                    .header("Content-Type", "audio/mpeg")
                    .header("Content-Disposition", "inline; filename=\"" + file.getName() + "\"")
                    .body(out);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new PayLoad("Error While Fetching The Audio File", "Failure", LocalDateTime.now(), null)
            );
        }
    }

    @DeleteMapping("/delete/{songId}")
    public ResponseEntity<?> deleteSong(@PathVariable Integer songId, Principal principal) {
        try {
            User user = this.userRepository.findByEmail(principal.getName());
            Song song = this.songRepository.findById(songId).orElse(null);

            if (song == null) {
                return ResponseEntity.ok(
                        new PayLoad("No Such Song", "Success", LocalDateTime.now(), null)
                );
            }

            if (!user.getSongs().contains(song)) {
                return ResponseEntity.ok(
                        new PayLoad("You Cant Delete This Song", "Success", LocalDateTime.now(), null)
                );
            }

            user.getSongs().remove(song);
            this.userRepository.save(user);
            return ResponseEntity.ok(
                    new PayLoad("Deleted The Song Successfully", "Success", LocalDateTime.now(), song.getTitle())
            );
        } catch (Exception e) {
            return ResponseEntity.ok(
                    new PayLoad("Error While Deleting The Song", "Failure", LocalDateTime.now(), null)
            );
        }
    }

    @GetMapping("/searchTitle/{title}")
    public ResponseEntity<?> searchByTitle(@PathVariable String title){
        try{
            List<Song> songs = this.songRepository.findByTitle(title);

            if(songs.isEmpty()){
                return ResponseEntity.ok(
                        new PayLoad("No Song With This Title","Success",LocalDateTime.now(),songs)
                );
            }

            return ResponseEntity.ok(
                    new PayLoad("All Songs","Success",LocalDateTime.now(),songs)
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new PayLoad("Error While Searching The song","Failure",LocalDateTime.now(),null)
            );
        }
    }

    @GetMapping("/searchArtist/{artist}")
    public ResponseEntity<?> searchByArtist(@PathVariable String artist){
        try{
            List<Song> songs = this.songRepository.findByArtist(artist);

            if(songs.isEmpty()){
                return ResponseEntity.ok(
                        new PayLoad("No Song Composed by This Artist","Success",LocalDateTime.now(),songs)
                );
            }

            return ResponseEntity.ok(
                    new PayLoad("All Songs By Such Artist","Success",LocalDateTime.now(),songs)
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new PayLoad("Error While Searching The song","Failure",LocalDateTime.now(),null)
            );
        }
    }
}
